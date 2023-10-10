package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.File.UserFileVO;
import com.example.fileUpload.repository.FileDao;
import com.example.fileUpload.service.FileDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileDownloadServiceImpl implements FileDownloadService {

    private final FileDao fileDao;

    @Value("${Save-Directory}")
    private String dir;

    @Override
    public String downloadFile(String userName, String fileName){

        String downloadPath = dir+ File.separator+"download"+File.separator+userName+File.separator;


        return downloadPath+fileDao.selectOriginalFileName(userName, fileName);
    }

    @Override
    public List<UserFileVO> showAcceptedFiles(String userName) {
        return fileDao.acceptedFiles(userName);
    }
}
