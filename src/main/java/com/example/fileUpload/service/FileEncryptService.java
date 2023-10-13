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
    void createRSAKeyPair() throws NoSuchAlgorithmException;
    void storedRSAKeyPair();
    void encryptFile(FileDto fileDto) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException;
    void decryptFile(Long id);

    void normalFileDownload(Long id);

}
