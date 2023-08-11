package com.example.fileUpload.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileUploadService {

    void fileUpload(MultipartFile multipartFile);

    List<Map<String, String>> printAll();

    Map<String, String> printOne(int id);
}
