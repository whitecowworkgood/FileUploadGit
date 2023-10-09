package com.example.fileUpload.service;

import com.example.fileUpload.model.FileVO;

import java.util.List;

public interface FileDownloadService {

    void downloadFile();

    List<FileVO> showAcceptedFiles(String userName);

}
