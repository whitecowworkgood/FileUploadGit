package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.FileVO;
import com.example.fileUpload.repository.FileDao;
import com.example.fileUpload.service.FileDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void downloadFile(){

    }

    @Override
    public List<FileVO> showAcceptedFiles(String userName) {
        return fileDao.acceptedFiles(userName);
    }
}
