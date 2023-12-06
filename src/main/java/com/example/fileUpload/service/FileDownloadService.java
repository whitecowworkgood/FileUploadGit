package com.example.fileUpload.service;

import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.File.UserFileVO;
import com.example.fileUpload.service.serviceImpl.FileDownloadServiceImpl;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileDownloadService {

    Boolean isDownloadAble(Long id);

    void decreaseCountNum(Long id);

    String getFileName(Long id);

    Resource downloadFile(Long id);

    List<UserFileVO> showAcceptedFiles(String userName);

}
