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

    private final StringBuffer stringBuffer = new StringBuffer();

    private Path fileStorageLocation = null;
    private Resource fileResource = null;

    private Long id = null;

    private String fileName = null;
    private String userName = null;


    @Value("${Save-Directory}")
    private String baseDir;

    @Override
    public void setParameter(String userName, Long id){
        this.userName = userName;
        this.id = id;
    }

    @Override
    public String getFileName(){

        try{
            fileName = this.fileDao.printFileInfo(this.id, this.userName).getOriginalFileName();

        }catch(NullPointerException e){
            ExceptionUtils.getStackTrace(e);
            fileName = "";
        }
        return fileName;
    }

    @Override
    public Resource downloadFile(Long id){

        try {
            stringBuffer.append(this.baseDir)
                    .append(File.separator)
                    .append("download")
                    .append(File.separator)
                    .append(this.userName)
                    .append(File.separator)
                    .append(fileDao.printFileInfo(this.id, this.userName).getUUIDFileName());

            this.fileStorageLocation = Path.of(stringBuffer.toString());
            this.fileResource = new UrlResource(this.fileStorageLocation.toUri());

        } catch (MalformedURLException | NullPointerException e) {
            ExceptionUtils.getStackTrace(e);

        }
        finally {
            stringBuffer.delete(0, stringBuffer.length());
        }

        return this.fileResource;

    }

    @Override
    public List<UserFileVO> showAcceptedFiles(String userName) {
        return this.fileDao.acceptedFiles(userName);
    }

    @Override
    public UserFileVO getUserFileVO(Long id){
        return this.fileDao.acceptedFilesById(id);
    }

    @Override
    public void decreaseCountNum(Long id) {
        this.fileDao.decreaseCountNum(id);
    }
}
