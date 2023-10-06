package com.example.fileUpload.util.Encrypt;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.security.*;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@Slf4j
@Component
public class RSA {
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

    public void freeResource(){
        log.info("자원 초기화 진행");
        stringKeypair = null;
        publicKey = null;
        privateKey = null;
        stringPublicKey = null;
        stringPrivateKey = null;
    }


}
