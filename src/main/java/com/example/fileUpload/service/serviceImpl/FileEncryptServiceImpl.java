package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.repository.EncryptDao;
import com.example.fileUpload.service.FileEncryptService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileEncryptServiceImpl implements FileEncryptService {

    @Value("${Save-Directory}")
    private String dir;

    private final EncryptDao encryptDao;


    private static ConcurrentHashMap<String, String> stringKeypair = new ConcurrentHashMap<>();

    private String macAddress = null;
    private SecretKey secretKey;
    byte[] encryptedBytes;

    private PublicKey publicKey = null;
    private PrivateKey privateKey = null;
    private String stringPublicKey = null;
    private String stringPrivateKey = null;
    private String encryptKey = null;

    private FileInputStream inputFileStream = null;
    private FileOutputStream encryptedFileStream = null;

    @Override
    public void encryptFile(FileDto fileDto) throws IOException {

        try{

            byte[] publicKeyBytes = Base64.getDecoder().decode(encryptKey);


            // 바이트 배열을 공개 키로 변환
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            inputFileStream = new FileInputStream(fileDto.getFileSavePath());
            encryptedFileStream = new FileOutputStream(dir+File.separator+"temp"+File.separator+fileDto.getUUIDFileName());

            byte[] buffer = new byte[245];

            int bytesRead;
            while ((bytesRead = inputFileStream.read(buffer)) != -1) {
                byte[] encryptedBytes = cipher.doFinal(buffer, 0, bytesRead);
                encryptedFileStream.write(encryptedBytes);
            }
            encryptedFileStream.write(encryptedBytes);

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | IOException |
                 BadPaddingException | InvalidKeyException | InvalidKeySpecException e) {

            ExceptionUtils.getStackTrace(e);

        } finally {
            IOUtils.closeQuietly(inputFileStream);
            IOUtils.closeQuietly(encryptedFileStream);
            encryptKey=null;

            Files.deleteIfExists(Path.of(fileDto.getFileSavePath()));
            Files.move(Path.of(dir+File.separator+"temp"+File.separator+fileDto.getUUIDFileName()), Path.of(fileDto.getFileSavePath()), StandardCopyOption.REPLACE_EXISTING);

        }

    }

    @Override
    @SneakyThrows
    public void decryptFile(String privateKeyString) {

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        String encryptedFilePath = "C:\\files\\temp\\aa58e51c-d851-4bbf-aac3-d8842a6d9603.docx";
        String decryptedFilePath = "C:\\files\\test.docx";

        try (FileInputStream encryptedFileStream = new FileInputStream(encryptedFilePath);
             FileOutputStream decryptedFileStream = new FileOutputStream(decryptedFilePath)) {

            byte[] inputBuffer = new byte[256];
            int bytesRead;

            while ((bytesRead = encryptedFileStream.read(inputBuffer)) != -1) {
                byte[] decryptedBytes = cipher.doFinal(inputBuffer, 0, bytesRead);
                decryptedFileStream.write(decryptedBytes);
            }
        }finally {
            Files.deleteIfExists(Path.of(encryptedFilePath));
        }
    }

    @Override
    public void getMacAddress() throws SocketException, UnknownHostException {

        InetAddress ip= InetAddress.getLocalHost();

        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        byte[] mac = network.getHardwareAddress();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        macAddress = sb.toString();

    }

    @Override
    public void generateKEK() throws NoSuchAlgorithmException {

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(macAddress.getBytes());

        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);

        secretKey = new SecretKeySpec(randomBytes, "AES");
    }

    @Override
    public void createRSAKeyPair() throws NoSuchAlgorithmException {

        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

        stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        stringKeypair.put("publicKey", stringPublicKey);
        stringKeypair.put("privateKey", stringPrivateKey);

    }

    @Override
    public void storedRSAKeyPair() {

        try {
            createRSAKeyPair();

        } catch (Exception e) {
            ExceptionUtils.getStackTrace(e);

        }finally {
            encryptDao.saveRSAKey(stringKeypair);
            encryptKey = stringPublicKey;
            encryptKEK();
            clearData();
        }
    }

    private void clearData(){
        publicKey = null;
        privateKey = null;

        stringPublicKey = null;
        stringPrivateKey  = null;
        //encryptKey=null;
        stringKeypair.clear();
    }

    @SneakyThrows // 일단 넣어둠
    @Override
    public void encryptKEK() {

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        encryptedBytes = cipher.doFinal(encryptKey.getBytes());

    }
    @SneakyThrows // 일단 넣어둠
    @Override
    public String decryptKEK(byte[] encryptedBytes) {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return new String(cipher.doFinal(encryptedBytes));
    }
}
