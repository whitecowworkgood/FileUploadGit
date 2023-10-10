package com.example.fileUpload.service;

import com.example.fileUpload.model.File.FileVO;

import java.util.List;

public interface AdminService {
    List<FileVO> printBeforeAcceptFiles();

    void acceptFile(Long id);
}
