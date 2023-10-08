package com.example.fileUpload.util.Encrypt;

import com.example.fileUpload.repository.EncryptDao;
import lombok.NoArgsConstructor;
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
public class RSA {

    private final EncryptDao encryptDao;

    private ConcurrentHashMap<String, String> stringKeypair = new ConcurrentHashMap<>();
    private PublicKey publicKey = null;
    private PrivateKey privateKey = null;
    private String stringPublicKey = null;
    private String stringPrivateKey = null;


    public ConcurrentHashMap<String, String> createRSAKeyPair() {

        try{
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


        }catch(NoSuchAlgorithmException e){
            ExceptionUtils.getStackTrace(e);

        }

        return stringKeypair;
    }

    public PrivateKey getPrivateKey(byte[] data) {
        long value = 0;
        PrivateKey privateKey = null;
        for (int i = 0; i < data.length; i++) {
            value = (value << 8) | (data[i] & 0xFF);
        }
        try{
            byte[] privateKeyBytes = Base64.getDecoder().decode(encryptDao.findPrivateKey(value));

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(privateKeySpec);

        }catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            ExceptionUtils.getStackTrace(e);
        }

        return privateKey;
    }


    public void freeResource(){
        log.info("자원 초기화 진행");
        stringKeypair = null;
        publicKey = null;
        privateKey = null;
        stringPublicKey = null;
        stringPrivateKey = null;
    }


}
