package com.example.fileUpload.util.Encrypt;

import com.example.fileUpload.message.ResultMessage;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.security.SecureRandom;

public class AES {
    private static final int AES_SIZE = 32;
    private static final int IV_SIZE = 16;


    private static class LazyHolder {
        private static final AES instance = new AES();
    }
    private AES() {
    }

    public static AES getInstance() {
        return AES.LazyHolder.instance;
    }

    public SecretKey generateAESKey() {
        try {
            byte[] keyData = new byte[AES_SIZE / 8]; // 비트 단위로 크기 조정
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            secureRandom.nextBytes(keyData);
            return new SecretKeySpec(keyData, "AES");
        } catch (NoSuchAlgorithmException e) {
            // 알고리즘이 지원되지 않을 때의 처리
            throw new RuntimeException("AES algorithm not supported");
        }
    }

    public IvParameterSpec generateIV() {
        try {
            byte[] ivBytes = new byte[IV_SIZE / 8]; // 비트 단위로 크기 조정
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            secureRandom.nextBytes(ivBytes);
            return new IvParameterSpec(ivBytes);
        } catch (NoSuchAlgorithmException e) {
            // 알고리즘이 지원되지 않을 때의 처리
            throw new RuntimeException("SecureRandom algorithm not supported");
        }
    }

}

