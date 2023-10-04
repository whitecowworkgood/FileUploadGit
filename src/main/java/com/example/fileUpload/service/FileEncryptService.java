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

public interface FileEncryptService {

    void encryptFile(FileDto fileDto) throws IOException;

    boolean decryptFile();

    void getMacAddress() throws SocketException, UnknownHostException;

    void generateKEK() throws NoSuchAlgorithmException;
    void createRSAKeyPair() throws NoSuchAlgorithmException;

    void storedRSAKeyPair() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    void encryptKEK() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException;

    String decryptKEK(byte[] encryptedBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
}
