package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.File.UserFileVO;
import com.example.fileUpload.repository.FileEntityDAO;
import com.example.fileUpload.service.FileDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileDownloadServiceImpl implements FileDownloadService {

    private final FileEntityDAO fileEntityDao;
    private final AuthService authService;

    private final StringBuffer stringBuffer = new StringBuffer();


    @Value("${Save-Directory}")
    private String baseDir;

    @Override
    public String getFileName(Long id){

        String fileName = null;

        try{
            String currentUserName = authService.getUserNameWeb();

            fileName = this.fileEntityDao.printFileInfo(id, currentUserName).orElseThrow(NullPointerException::new).getOriginalFileName();

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
                    .append(this.fileEntityDao.printFileInfo(id, currentUserName).orElseThrow(NullPointerException::new).getUUIDFileName());


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
        return this.fileEntityDao.acceptedFiles(userName);
    }

    @Override
    public boolean isDownloadAble(Long id){
        return this.fileEntityDao.acceptedFilesById(id).getCountNum()>0;
    }

    @Override
    public void decreaseCountNum(Long id) {
        this.fileEntityDao.decreaseCountNum(id);
    }

}
