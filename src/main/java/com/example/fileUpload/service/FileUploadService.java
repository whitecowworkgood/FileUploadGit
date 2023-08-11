package com.example.fileUpload.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    void fileUpload(MultipartFile multipartFile);

    Object printAll();

    Object printOne(Long id);

    void deleteOne(Long id);
}
