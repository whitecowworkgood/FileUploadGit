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
    static final int ENCRYPTION_BUFFER_SIZE = 1024;
    static final int READ_OPTION_SIZE = 516;
    static final int READ_ENCRYPT_IV_SIZE = 256;
    static final int READ_ENCRYPT_KEY_SIZE = 256;
    static final int READ_RSA_INDEX_SIZE = 4;

    void encryptFile(FileDto fileDto) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException;

    void decryptFile(Long id) throws IOException;



}
