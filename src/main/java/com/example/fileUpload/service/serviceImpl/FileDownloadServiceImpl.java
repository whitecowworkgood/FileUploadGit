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
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileDownloadServiceImpl implements FileDownloadService {

    private final FileDao fileDao;
    private final AuthService authService;

    private final StringBuffer stringBuffer = new StringBuffer();


    @Value("${Save-Directory}")
    private String baseDir;

    @Override
    public String getFileName(Long id){

        String fileName = null;

        try{
            String currentUserName = authService.getUserNameWeb();

            fileName = this.fileDao.printFileInfo(id, currentUserName).orElseThrow(NullPointerException::new).getOriginalFileName();

        }catch(NullPointerException e){
            ExceptionUtils.getStackTrace(e);
            fileName = "";
        }
        return fileName;
    }

    @Override
    public Resource downloadFile(Long id){
        Resource fileResource = null;

        try {
            String currentUserName = authService.getUserNameWeb();
            this.stringBuffer.append(this.baseDir)
                    .append(File.separator)
                    .append("download")
                    .append(File.separator)
                    .append(currentUserName)
                    .append(File.separator)
                    .append(this.fileDao.printFileInfo(id, currentUserName).orElseThrow(NullPointerException::new).getUUIDFileName());


            Path fileStorageLocation = Path.of(this.stringBuffer.toString());
            fileResource = new UrlResource(fileStorageLocation.toUri());

        } catch (MalformedURLException | NullPointerException e) {
            ExceptionUtils.getStackTrace(e);

        }
        finally {
            this.stringBuffer.delete(0, this.stringBuffer.length());
        }

        return fileResource;

    }

    @Override
    public List<UserFileVO> showAcceptedFiles(String userName) {
        return this.fileDao.acceptedFiles(userName);
    }

    @Override
    public Boolean isDownloadAble(Long id){
        return this.fileDao.acceptedFilesById(id).getCountNum()>0;
    }

    @Override
    public void decreaseCountNum(Long id) {
        this.fileDao.decreaseCountNum(id);
    }

}
