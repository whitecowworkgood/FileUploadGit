package com.example.fileUpload.util.Encrypt;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.security.SecureRandom;

public class AES {
    private static final int AES_SIZE = 32;
    private static final int IV_SIZE = 16;
    private SecureRandom secureRandom;

    public AES() throws NoSuchAlgorithmException {
        secureRandom = SecureRandom.getInstanceStrong();
    }

    public SecretKey generateAESKey() {
        byte[] keyData = new byte[AES_SIZE]; // 256비트 키
        secureRandom.nextBytes(keyData);
        return new SecretKeySpec(keyData, "AES");
    }

    public IvParameterSpec generateIV() {
        byte[] ivBytes = new byte[IV_SIZE];
        secureRandom.nextBytes(ivBytes);
        return new IvParameterSpec(ivBytes);
    }
}


/*public class AES {
    private static final int AES_SIZE = 32;
    private static final int IV_SIZE = 16;

    public SecretKey generateAESKey() throws NoSuchAlgorithmException {

        byte[] keyData = new byte[AES_SIZE]; // 256비트 키
        SecureRandom.getInstanceStrong().nextBytes(keyData);

        return new SecretKeySpec(keyData, "AES");

    }

    public IvParameterSpec generateIV() throws NoSuchAlgorithmException {

        byte[] ivBytes = new byte[IV_SIZE];
        SecureRandom.getInstanceStrong().nextBytes(ivBytes);

        return new IvParameterSpec(ivBytes);
    }
}*/
