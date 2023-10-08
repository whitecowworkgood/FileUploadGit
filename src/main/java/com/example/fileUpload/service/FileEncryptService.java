package com.example.fileUpload.service;

import com.example.fileUpload.model.FileDto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

public interface FileEncryptService {
    void createRSAKeyPair() throws NoSuchAlgorithmException;
    void storedRSAKeyPair();
    void encryptFile(FileDto fileDto) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException;
    void decryptFile(Long id);

}
