package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.repository.RSAKeysDAO;
import com.example.fileUpload.repository.FileEntityDAO;
import com.example.fileUpload.service.FileEncryptService;
import com.example.fileUpload.util.Encrypt.AES;
import com.example.fileUpload.util.Encrypt.RSAFactory;
import com.google.common.primitives.Bytes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.security.*;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileEncryptServiceImpl implements FileEncryptService {

    @Value("${Download-Directory}")
    private String downloadDir;

    private final String CIPHER_TRANSFORMATION_MAIN = "AES/CBC/PKCS5Padding";
    private final String CIPHER_TRANSFORMATION_OPTION = "RSA/ECB/PKCS1Padding";

    private final RSAFactory rsaFactory;
    private final FileEntityDAO fileEntityDao;
    private final RSAKeysDAO RSAKeysDao;
    private final AES aes;

    @Override
    public void encryptFile(FileDto fileDto){

        FileInputStream inputFileStream = null;
        FileOutputStream encryptedOutputStream = null;

        byte[] buffer = new byte[ENCRYPTION_BUFFER_SIZE];
        int bytesRead = -1;

        try{
            SecretKey fileEncryptKey = aes.generateAESKey();
            IvParameterSpec ivSpec = aes.generateIV();

            Cipher encryptCipher = getEncryptCipher(fileEncryptKey, ivSpec);

            inputFileStream = new FileInputStream(fileDto.getFileTempPath());
            encryptedOutputStream = new FileOutputStream(fileDto.getFileSavePath());

            while ((bytesRead = inputFileStream.read(buffer)) != -1) {

                byte[] encryptedBytes = encryptCipher.update(buffer, 0, bytesRead);
                if (encryptedBytes != null) {
                    encryptedOutputStream.write(encryptedBytes);
                }
            }

            byte[] finalEncryptedBytes = encryptCipher.doFinal();
            if (finalEncryptedBytes != null) {
                encryptedOutputStream.write(finalEncryptedBytes);
            }

            encryptedOutputStream.write(appendEncryptedOptions(fileEncryptKey, ivSpec));

        } catch (IllegalBlockSizeException | IOException | BadPaddingException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException("파일 암호화에 실패하였습니다.");

        }finally{
            IOUtils.closeQuietly(inputFileStream);
            IOUtils.closeQuietly(encryptedOutputStream);
        }
    }

    @Override
    public void decryptFile(Long id) {

        FileOutputStream decryptedOutputStream = null;
        RandomAccessFile randomAccessFile = null;
        byte[] decryptedBuffer = new byte[ENCRYPTION_BUFFER_SIZE];
        int totalBytesRead = 0;

        try{
            String outputFilePath = appendDecryptedPath(id);

            randomAccessFile = new RandomAccessFile(this.fileEntityDao.printFileOne(id).getFileSavePath(), "r");
            decryptedOutputStream = new FileOutputStream(outputFilePath);

            long encryptedContentPosition = calcPosition(randomAccessFile.length());
            Cipher decryptCipher =  getDecryptCipher(randomAccessFile, encryptedContentPosition);

            while (totalBytesRead < encryptedContentPosition) {
                int bytesRead;

                if (encryptedContentPosition - totalBytesRead >= ENCRYPTION_BUFFER_SIZE) {
                    bytesRead = randomAccessFile.read(decryptedBuffer, 0, ENCRYPTION_BUFFER_SIZE);
                } else {
                    bytesRead = randomAccessFile.read(decryptedBuffer, 0, (int) (encryptedContentPosition - totalBytesRead));
                }

                byte[] decryptedBytes = decryptCipher.update(decryptedBuffer, 0, bytesRead);

                if(decryptedBytes != null){
                    decryptedOutputStream.write(decryptedBytes);
                }

                totalBytesRead += bytesRead;
            }

            byte[] finalOutBuffer = decryptCipher.doFinal();

            if (finalOutBuffer != null) {
                decryptedOutputStream.write(finalOutBuffer);
            }


        } catch (IOException |  IllegalBlockSizeException | BadPaddingException e) {
            ExceptionUtils.getStackTrace(e);

        } finally {
            IOUtils.closeQuietly(decryptedOutputStream);
            IOUtils.closeQuietly(randomAccessFile);

        }

    }

    private byte[] appendEncryptedOptions(SecretKey fileEncryptKey, IvParameterSpec ivSpec){

        long latestInsertedId = this.RSAKeysDao.getLatestId();

        byte[] RSAIndex = ByteBuffer.allocate(4).putInt(Math.toIntExact(latestInsertedId)).array();
        byte[] IV = encryptOptions(ivSpec.getIV());
        byte[] AESKey = encryptOptions(fileEncryptKey.getEncoded());

        return Bytes.concat(RSAIndex, IV, AESKey);

    }

    private byte[] encryptOptions(byte[] byteData){

        try{
            PublicKey publicKey = this.rsaFactory.getPublicKey();

            Cipher cipher = Cipher.getInstance(this.CIPHER_TRANSFORMATION_OPTION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            return cipher.doFinal(byteData);

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException e) {

            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException("옵션 암호화에 실패하였습니다.");
        }

    }

    private byte[] decryptOptions(byte[] byteData, PrivateKey privateKey){

        try{
            Cipher cipher = Cipher.getInstance(this.CIPHER_TRANSFORMATION_OPTION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            return cipher.doFinal(byteData);

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException e) {

            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException("옵션 복호화에 실패하였습니다.");
        }

    }

    private Cipher getEncryptCipher(SecretKey fileEncryptKey, IvParameterSpec ivSpec){

        try {
            Cipher cipher = Cipher.getInstance(this.CIPHER_TRANSFORMATION_MAIN);
            cipher.init(Cipher.ENCRYPT_MODE, fileEncryptKey, ivSpec);

            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException("암호화용 Cipher 생성에 실패하였습니다.");
        }

    }

    private Cipher getDecryptCipher(RandomAccessFile randomAccessFile, long encryptedContentPosition){

        try {
            randomAccessFile.seek(encryptedContentPosition);
            byte[] optionsBuffer =  new byte[]{randomAccessFile.readByte()};

            PrivateKey privateKey = this.rsaFactory.getPrivateKey(getRSAId(optionsBuffer));
            IvParameterSpec ivSpec = new IvParameterSpec(decryptOptions(getIV(optionsBuffer), privateKey));
            SecretKey fileEncryptKey = new SecretKeySpec(decryptOptions(getAESKey(optionsBuffer), privateKey), "AES");

            Cipher cipher = Cipher.getInstance(this.CIPHER_TRANSFORMATION_MAIN);
            cipher.init(Cipher.DECRYPT_MODE, fileEncryptKey, ivSpec);

            return cipher;

        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException("암호화 되어있는 옵션 정보를 가져오는데 실패하였습니다.");
        }catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                InvalidKeyException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException("복호화용 Cipher 생성에 실패하였습니다.");
        }

    }

    private long calcPosition(long fileSize){
        return fileSize - this.READ_OPTION_SIZE;
    }

    private byte[] getIV(byte[] input){
        byte[] ivSpecByte = new byte[READ_ENCRYPT_IV_SIZE];
        System.arraycopy(input, 4, ivSpecByte, 0, READ_ENCRYPT_IV_SIZE);
        return ivSpecByte;

    }
    private byte[] getAESKey(byte[] input){
        byte[] AESKeyByte = new byte[READ_ENCRYPT_KEY_SIZE];
        System.arraycopy(input, 260, AESKeyByte, 0, READ_ENCRYPT_KEY_SIZE);
        return AESKeyByte;
    }
    private byte[] getRSAId(byte[] input){
        byte[] RSAIndex = new byte[READ_RSA_INDEX_SIZE];
        System.arraycopy(input, 0, RSAIndex, 0, READ_RSA_INDEX_SIZE);
        return RSAIndex;
    }

    private String appendDecryptedPath(Long id){

        FileVO fileVO = this.fileEntityDao.printFileOne(id);
        String userName = fileVO.getUserName();
        String uuidFileName = fileVO.getUUIDFileName();

        String downloadFileUserPath = Paths.get(this.downloadDir, userName).toString();
        generateFolder(downloadFileUserPath);

        return Paths.get(downloadFileUserPath, uuidFileName).toString();
    }



}
