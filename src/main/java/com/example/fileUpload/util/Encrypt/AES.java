package com.example.fileUpload.util.Encrypt;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class AES {
    private static final int AES_SIZE = 32;
    private static final int IV_SIZE = 16;


    public SecretKey generateAESKey() {
        try {
            byte[] keyData = new byte[AES_SIZE];
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            secureRandom.nextBytes(keyData);

            return new SecretKeySpec(keyData, "AES");

        } catch (NoSuchAlgorithmException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException("AES 알고리즘을 지원하지 않습니다.");
        }
    }

    public IvParameterSpec generateIV() {
        try {
            byte[] ivBytes = new byte[IV_SIZE];
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            secureRandom.nextBytes(ivBytes);

            return new IvParameterSpec(ivBytes);

        } catch (NoSuchAlgorithmException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException("알고리즘을 지원하지 않습니다.");
        }
    }

}

