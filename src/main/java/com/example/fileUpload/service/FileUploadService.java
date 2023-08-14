package com.example.fileUpload.service;

import com.example.fileUpload.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    void fileUpload(MultipartFile multipartFile);

    Object printAll();

    public FileEntity printOne(Long id);

    void deleteOne(Long id);
}
