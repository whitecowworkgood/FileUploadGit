package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.documentParser.FileProcessor;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.File.FileVO;

import com.example.fileUpload.model.Ole.OleVO;
import com.example.fileUpload.repository.FileDao;
import com.example.fileUpload.repository.OleDao;
import com.example.fileUpload.service.FileEncryptService;
import com.example.fileUpload.service.FileUploadService;
import com.example.fileUpload.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileUploadServiceImpl implements FileUploadService {

    private final FileDao fileDao; //mybatis
    private final OleDao oleDao; //mybatis
    private final FileProcessor fileProcessor;
    private final FileEncryptService fileEncryptService;

    private final StringBuffer stringBuffer = new StringBuffer();

    @Value("${Save-Directory}")
    private String baseDir;

    @SneakyThrows
    @Override
    @Transactional
    public synchronized void fileUpload(FileDto fileDto) {
        try {

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
        }
    }


    @Override
    public synchronized FileVO printFileOne(Long id) {

        Optional<FileVO> optionalFileVO = Optional.ofNullable(this.fileDao.printFileOne(id));

        return optionalFileVO.orElse(null);
    }

    @Override
    public synchronized List<OleVO> printOleAll(Long id) {

        return this.oleDao.selectById(id);
    }

    @Override
    public synchronized boolean deleteOne(Long id) {
        boolean fileResult;
        boolean oleResult;

        FileVO fileVO = this.fileDao.printFileOne(id);
        if (fileVO == null) {
            return false;
        }
        try{

            if (!Files.exists(Path.of(fileVO.getFileSavePath())) || !FileUtil.isPathValidForStorage(this.baseDir, fileVO.getFileSavePath())) {
                return false;
            }

            Files.delete(Path.of(fileVO.getFileSavePath()));
            fileResult = this.fileDao.deleteById(fileVO.getId());

            Files.delete(Path.of(fileVO.getFileOlePath()));
            oleResult = this.oleDao.deleteById(fileVO.getId());


        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
            return false;
        }

        return fileResult && oleResult;
    }

    private void validateFileDto(FileDto fileDto) throws FileUploadException {
        if (fileDto.getFileData().isEmpty() || !FileUtil.isPathValidForStorage(this.baseDir, fileDto.getFileSavePath())) {
            throw new FileUploadException();
        }
    }


}