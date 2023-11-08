package com.example.fileUpload.service;

import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.File.UserFileVO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileDownloadService {

    UserFileVO getUserFileVO(Long id);

    void decreaseCountNum(Long id);

    String getFileName();

    void setParameter(String userName, Long id);

    Resource downloadFile(Long id);

    List<UserFileVO> showAcceptedFiles(String userName);

}
