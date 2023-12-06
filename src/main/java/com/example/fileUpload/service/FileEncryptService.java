package com.example.fileUpload.service;

import com.example.fileUpload.model.File.FileDto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface FileEncryptService {
    int ENCRYPTION_BUFFER_SIZE = 1024;
    int READ_OPTION_SIZE = 516;
    int READ_ENCRYPT_IV_SIZE = 256;
    int READ_ENCRYPT_KEY_SIZE = 256;
    int READ_RSA_INDEX_SIZE = 4;

    void encryptFile(FileDto fileDto) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException;

    void decryptFile(Long id);



}
