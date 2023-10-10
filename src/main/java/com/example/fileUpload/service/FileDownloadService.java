package com.example.fileUpload.service;

import com.example.fileUpload.model.File.UserFileVO;

import java.util.List;

public interface FileDownloadService {

    String downloadFile(String userName, String fileName);

    List<UserFileVO> showAcceptedFiles(String userName);

}
