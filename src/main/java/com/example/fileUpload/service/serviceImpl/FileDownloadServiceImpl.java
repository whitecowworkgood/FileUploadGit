package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.File.UserFileVO;
import com.example.fileUpload.repository.FileDao;
import com.example.fileUpload.service.FileDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileDownloadServiceImpl implements FileDownloadService {

    private final FileDao fileDao;

    private Path fileStorageLocation = null;
    private Resource fileResource = null;

    private Long id = null;
    private String userName = null;


    @Value("${Save-Directory}")
    private String dir;

    @Override
    public void setParameter(String userName, Long id){
        this.id = id;
        this.userName = userName;
    }

    public String getFileName(){

        return fileDao.printFileOne(this.id).getOriginalFileName();
        //return fileDao.selectUUIDFileNameByOriginalFileName();
    }

    @Override
    public Resource downloadFile(Long id){

        String downloadPath = this.dir+ File.separator+"download"+File.separator+this.userName+File.separator;
        this.fileStorageLocation = Path.of(downloadPath+fileDao.printFileOne(this.id).getUUIDFileName());

        try {
            this.fileResource = new UrlResource(this.fileStorageLocation.toUri());

        } catch (MalformedURLException e) {
            ExceptionUtils.getStackTrace(e);
        }

        return this.fileResource;

    }

    @Override
    public List<UserFileVO> showAcceptedFiles(String userName) {
        return fileDao.acceptedFiles(userName);
    }

    @Override
    public UserFileVO getUserFileVO(Long id){
        return fileDao.acceptedFilesById(id);
    }

    @Override
    public void decreaseCountNum(Long id) {
        fileDao.decreaseCountNum(id);
    }
}
