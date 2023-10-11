package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.repository.EncryptDao;
import com.example.fileUpload.repository.FileDao;
import com.example.fileUpload.service.FileEncryptService;
import com.example.fileUpload.util.Encrypt.RSA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.fileUpload.util.DirectoryChecker.generateUserFolder;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileEncryptServiceImpl implements FileEncryptService {

    private static final int ENCRYPTION_BUFFER_SIZE = 1024;
    private static final int AES_SIZE = 32;
    private static final int IV_SIZE = 16;


    private static final int READ_OPTION_SIZE = 516;
    private static final int READ_ENCRYPT_IV_SIZE = 256;
    private static final int READ_ENCRYPT_KEY_SIZE = 256;
    private static final int READ_RSA_INDEX_SIZE = 4;

    @Value("${Save-Directory}")
    private String baseDir;

    private final RSA rsa;
    private final EncryptDao encryptDao;
    private final FileDao fileDao;

    private ConcurrentHashMap<String, String> stringKeypair = null;
    private Cipher cipher = null;
    private Long latestInsertedId = null;

    private String publicKey = null;

    private SecretKey fileEncryptKey = null;
    private IvParameterSpec ivSpec = null;
    private PrivateKey privateKey = null;

    private FileInputStream inputFileStream = null;
    private FileOutputStream encryptedFileStream = null;

    private RandomAccessFile randomAccessFile =null;

    @Override
    public void createRSAKeyPair() {

        this.stringKeypair = this.rsa.createRSAKeyPair();

        storedRSAKeyPair();

        this.rsa.freeResource();
    }

    @Override
    public void storedRSAKeyPair(){
        this.encryptDao.saveRSAKey(this.stringKeypair);

        int targetNum = this.stringKeypair.toString().indexOf("id=")+3;

        this.latestInsertedId = NumberUtils.toLong(this.stringKeypair.toString().substring(targetNum, this.stringKeypair.toString().length()-1));

        this.publicKey = this.stringKeypair.get("publicKey");
        this.stringKeypair = null;

    }

    @Override
    public void encryptFile(FileDto fileDto) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {

        generateAESKey();

        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.cipher.init(Cipher.ENCRYPT_MODE, fileEncryptKey, ivSpec);

        try{
            this.inputFileStream = new FileInputStream(fileDto.getFileSavePath());
            this.encryptedFileStream = new FileOutputStream(this.baseDir+File.separator+"temp"+File.separator+fileDto.getUUIDFileName());

            byte[] buffer = new byte[ENCRYPTION_BUFFER_SIZE];

            int bytesRead;

            while ((bytesRead = this.inputFileStream.read(buffer)) != -1) {

                byte[] encryptedBytes = this.cipher.update(buffer, 0, bytesRead);

                if(encryptedBytes != null){
                    this.encryptedFileStream.write(encryptedBytes);
                }

            }

            byte[] outBytes = this.cipher.doFinal();

            if(outBytes != null){
                this.encryptedFileStream.write(outBytes);
            }

            this.encryptedFileStream.write(ByteBuffer.allocate(4).putInt(Math.toIntExact(this.latestInsertedId)).array());
            this.encryptedFileStream.write(encryptOptions(this.ivSpec.getIV()));
            this.encryptedFileStream.write(encryptOptions(this.fileEncryptKey.getEncoded()));

        } catch (IllegalBlockSizeException | IOException | BadPaddingException e) {
            ExceptionUtils.getStackTrace(e);

        }finally{
            IOUtils.closeQuietly(this.inputFileStream);
            IOUtils.closeQuietly(this.encryptedFileStream);
            this.fileEncryptKey=null;
            this.ivSpec=null;

            Files.deleteIfExists(Path.of(fileDto.getFileSavePath()));
            Files.move(Path.of(this.baseDir+File.separator+"temp"+File.separator+fileDto.getUUIDFileName()), Path.of(fileDto.getFileSavePath()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void decryptFile(Long id) {
        try{

            String downloadFileUserPath =this.baseDir+File.separator+"download"+
                    File.separator+this.fileDao.printFileOne(id).getUserName();

            generateUserFolder(downloadFileUserPath);

            this.randomAccessFile = new RandomAccessFile(this.fileDao.printFileOne(id).getFileSavePath(), "r");
            this.encryptedFileStream = new FileOutputStream(downloadFileUserPath+File.separator+
                    this.fileDao.printFileOne(id).getUUIDFileName());

            long fileSize = this.randomAccessFile.length();

            // 하위 516바이트를 읽기 시작할 위치를 계산합니다.
            long position = fileSize - READ_OPTION_SIZE;

            // 파일 포인터를 설정하여 하위 516바이트의 위치로 이동합니다.
            this.randomAccessFile.seek(position);

            // 516바이트를 읽어올 배열을 생성합니다.
            byte[] optionsBuffer = new byte[READ_OPTION_SIZE];

            // 파일에서 데이터를 읽어옵니다.
            this.randomAccessFile.read(optionsBuffer);


            // 4바이트, 256바이트, 256바이트로 분리합니다.
            byte[] RSAIndex = new byte[READ_RSA_INDEX_SIZE];
            byte[] ivSpecByte = new byte[READ_ENCRYPT_IV_SIZE];
            byte[] AESKeyByte = new byte[READ_ENCRYPT_KEY_SIZE];

            System.arraycopy(optionsBuffer, 0, RSAIndex, 0, 4);
            System.arraycopy(optionsBuffer, 4, ivSpecByte, 0, 256);
            System.arraycopy(optionsBuffer, 260, AESKeyByte, 0, 256);

            this.privateKey = this.rsa.getPrivateKey(RSAIndex);
            this.ivSpec = new IvParameterSpec(decryptOptions(ivSpecByte));
            this.fileEncryptKey = new SecretKeySpec(decryptOptions(AESKeyByte), "AES");

            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.cipher.init(Cipher.DECRYPT_MODE, fileEncryptKey, ivSpec);

            this.randomAccessFile.seek(0);

            byte[] decryptedBuffer = new byte[ENCRYPTION_BUFFER_SIZE];

            // 복호화할 데이터를 읽어옵니다.
            int totalBytesRead = 0;

            while (totalBytesRead < position) {
                int bytesRead;

                if (position - totalBytesRead >= ENCRYPTION_BUFFER_SIZE) {
                    bytesRead = this.randomAccessFile.read(decryptedBuffer, 0, ENCRYPTION_BUFFER_SIZE);
                } else {
                    bytesRead = this.randomAccessFile.read(decryptedBuffer, 0, (int) (position - totalBytesRead));
                }

                byte[] decryptedBytes = this.cipher.update(decryptedBuffer, 0, bytesRead);

                if(decryptedBytes != null){
                    this.encryptedFileStream.write(decryptedBytes);
                }

                totalBytesRead += bytesRead;
            }

            byte[] finalObuf = this.cipher.doFinal();
            if (finalObuf != null) {
                this.encryptedFileStream.write(finalObuf);
            }


        } catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                 NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            ExceptionUtils.getStackTrace(e);

        } finally {
            IOUtils.closeQuietly(this.inputFileStream);
            IOUtils.closeQuietly(this.encryptedFileStream);
            IOUtils.closeQuietly(this.randomAccessFile);

            this.cipher=null;
            this.privateKey = null;
            this.ivSpec = null;
            this.fileEncryptKey = null;

        }

    }

    private byte[] encryptOptions(byte[] byteData){

        byte[] encryptedBytes = null;

        try{
            //나중에 rsa생성 후, id값 가져오고, map정리 및 필요한 공개키는 별도의 변수로 저장
            byte[] publicKeyBytes = Base64.getDecoder().decode(this.publicKey);


            // 바이트 배열을 공개 키로 변환
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            encryptedBytes = cipher.doFinal(byteData);

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException | InvalidKeySpecException e) {

            ExceptionUtils.getStackTrace(e);

        }

        return encryptedBytes;
    }

    private byte[] decryptOptions(byte[] byteData){

        byte[] encryptedBytes = null;

        try{

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, this.privateKey);

            encryptedBytes = cipher.doFinal(byteData);

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException e) {

            ExceptionUtils.getStackTrace(e);

        }

        return encryptedBytes;
    }


    private void generateAESKey() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] keyData = new byte[AES_SIZE]; // 256비트 키
        secureRandom.nextBytes(keyData);
        this.fileEncryptKey = new SecretKeySpec(keyData, "AES");

        byte[] ivBytes = new byte[IV_SIZE];
        secureRandom.nextBytes(ivBytes);
        this.ivSpec = new IvParameterSpec(ivBytes);

    }
}
