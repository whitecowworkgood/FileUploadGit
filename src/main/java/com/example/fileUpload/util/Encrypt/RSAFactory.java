package com.example.fileUpload.util.Encrypt;

import com.example.fileUpload.repository.RSAKeysDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@Component
public class RSAFactory {

    private final int RSA_KEY_SIZE = 2048;
    private final String RSA_ALGORITHM = "RSA";
    private final String RSA_PUBLIC_KEY = "publicKey";
    private final String RSA_PRIVATE_KEY = "privateKey";

    private final RSAKeysDAO RSAKeysDao;

    private final ConcurrentHashMap<String, String> stringKeypair = new ConcurrentHashMap<>();

    public void createRSAKeyPair() {

        try{
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(this.RSA_ALGORITHM);
            keyPairGenerator.initialize(this.RSA_KEY_SIZE, secureRandom);
            KeyPair keyPair = keyPairGenerator.genKeyPair();

            String stringPublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String stringPrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            this.stringKeypair.put(this.RSA_PUBLIC_KEY, stringPublicKey);
            this.stringKeypair.put(this.RSA_PRIVATE_KEY, stringPrivateKey);

            this.RSAKeysDao.saveRSAKey(this.stringKeypair);

        }catch(NoSuchAlgorithmException e){
            ExceptionUtils.getStackTrace(e);

        }

    }

    public synchronized PublicKey getPublicKey(){

        String stringPublicKey = this.stringKeypair.get(this.RSA_PUBLIC_KEY);

        byte[] publicKeyBytes = Base64.getDecoder().decode(stringPublicKey);

        try {
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(this.RSA_ALGORITHM);

            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException("공개키를 가져오는데 문제가 발생하였습니다.");
        }

    }

    public synchronized PrivateKey getPrivateKey(byte[] data) {

        try{
            long value = 0;

            for (byte datum : data) {
                value = (value << 8) | (datum & 0xFF);
            }

            byte[] privateKeyBytes = Base64.getDecoder().decode(this.RSAKeysDao.findPrivateKey(value));

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(this.RSA_ALGORITHM);

            return keyFactory.generatePrivate(privateKeySpec);

        }catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException("개인키를 가져오는데 문제가 발생하였습니다.");
        }

    }

}
