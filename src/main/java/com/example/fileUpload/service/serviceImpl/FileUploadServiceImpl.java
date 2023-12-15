package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.documentParser.*;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.File.FileVO;

import com.example.fileUpload.model.Ole.OleVO;
import com.example.fileUpload.repository.FileEntityDAO;
import com.example.fileUpload.repository.OleEntryDAO;
import com.example.fileUpload.service.FileEncryptService;
import com.example.fileUpload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileUploadServiceImpl implements FileUploadService {

    private final FileEntityDAO fileEntityDao;
    private final OleEntryDAO oleEntryDao;
    private final FileProcessor fileProcessor;
    private final FileEncryptService fileEncryptService;
    private final AuthService authService;

    @SneakyThrows
    @Override
    @Transactional
    public synchronized void fileUpload(FileDto fileDto) {

        if(isInTemporaryFolder(fileDto.getFileTempPath())){

            try{
                String userName = authService.getUserNameWeb();
                fileDto.setUserName(userName);

                this.fileEntityDao.saveFile(fileDto);
                this.fileProcessor.createOleExtractorHandler(fileDto);
                this.fileEncryptService.encryptFile(fileDto);
            }catch(Exception e){
                ExceptionUtils.getStackTrace(e);
                log.error("에러발생");
            }
        }



        /*try {
            String userName = authService.getUserNameWeb();

            fileDto.setUserName(userName);
            //코드의 순서와 로직을 조금 바꿔서 적절하게 재구축 해보기
            validateFileDto(fileDto);

            fileDto.getFileData().transferTo(new File(fileDto.getFileSavePath()));

            if (!this.fileDao.saveFile(fileDto)) {
                throw new FileUploadException();
            }

            this.fileProcessor.createOleExtractorHandler(fileDto);
            this.fileEncryptService.encryptFile(fileDto);

        } catch (IOException | RuntimeException e) {
            ExceptionUtils.getStackTrace(e);

            stringBuffer.append(this.baseDir).append(File.separator).append(fileDto.getUUIDFileName());
            Files.delete(Path.of(stringBuffer.toString()));

           throw new FileUploadException("파일 업로드에 실패하였습니다.");

        }finally {
            stringBuffer.delete(0, stringBuffer.length());
        }*/
    }


    @Override
    public synchronized FileVO printFileOne(Long id) {

        Optional<FileVO> optionalFileVO = Optional.ofNullable(this.fileEntityDao.printFileOne(id));

        return optionalFileVO.orElseThrow(()->new RuntimeException("데이터 조회에 실패하였습니다."));
    }

    @Override
    public synchronized List<OleVO> printOleAll(Long id) {
        return this.oleEntryDao.selectById(id);
    }

    private boolean isInTemporaryFolder(String tempPath) {
        return Files.exists(Paths.get(tempPath));
    }


}