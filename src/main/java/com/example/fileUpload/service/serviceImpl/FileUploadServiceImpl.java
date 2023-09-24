package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.documentParser.FileProcessor;
import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.model.OleDto;
import com.example.fileUpload.model.FileVO;
import com.example.fileUpload.repository.FileDao;
import com.example.fileUpload.repository.OleDao;
import com.example.fileUpload.service.FileUploadService;
import com.example.fileUpload.util.ExternalFileMap;
import com.example.fileUpload.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

    @Value("${Save-Directory}")
    private String dir;
    //추후 프로젝트 경로나, c:\\경로에 폴더가 있는지 확인 후, 없으면 폴더 생성 후 파일 전송하기

    @Override
    @Transactional
    public synchronized boolean fileUpload(FileDto fileDto) {

        try {
            if (!fileDto.getFileData().isEmpty()) {

                if (FileUtil.isPathValidForStorage(dir, fileDto.getFileSavePath())) {

                    if (FileUtil.validateUploadedFileMimeType(fileDto)) {

                        fileDto.getFileData().transferTo(new File(fileDto.getFileSavePath()));

                        boolean fileResult = fileDao.saveFile(fileDto);

                        if(!Files.exists(Path.of(fileDto.getFileOlePath()))){
                            try{
                                //Folder.mkdir(); //폴더 생성합니다.
                                Files.createDirectories(Path.of(fileDto.getFileOlePath()));
                            }
                            catch(Exception e){
                                ExceptionUtils.getStackTrace(e);
                            }
                        }


                        fileProcessor.createOleExtractorHandler(fileDto);
                        ExternalFileMap.forEach(entry -> {

                            OleDto oleDto = OleDto.builder().superId(fileDto.getId())
                                    .originalFileName(entry.getKey())
                                    .UUIDFileName(entry.getValue())
                                    .build();



                            oleDao.insertOle(oleDto);

                        });
                        ExternalFileMap.resetMap();
                        return fileResult;
                        //return true;
                    }
                }
            }
        } catch (RuntimeException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            new File(dir+File.separator + fileDto.getUUIDFileName()).delete();

        } catch (IOException i) {
            log.error(ExceptionUtils.getStackTrace(i));
        }
        return false;
    }

    @Override
    public synchronized List<FileVO> printFileAll() {
        return fileDao.printFileAll();
    }

    @Override
    public synchronized FileVO printFileOne(Long id) {

        Optional<FileVO> optionalFileVO = Optional.ofNullable(fileDao.printFileOne(id));

        return optionalFileVO.orElse(null);
    }

    @Override
    public synchronized List<OleDto> printOleAll(Long id) {

        return oleDao.selectById(id);
    }

    @Override
    public synchronized boolean deleteOne(Long id) {

        FileVO fileVO = fileDao.printFileOne(id);
        if (fileVO == null) {
            return false;
        }

        String fileName = fileVO.getUUIDFileName();
        String savePath = fileVO.getFileOlePath();
        String fullPath = dir+File.separator + fileName;

        if (!FileUtil.isPathValidForStorage(dir, fullPath)) {
            return false;
        }

        File file = new File(fullPath);

        if (!(file.exists() && file.delete())) {
            log.warn(fileName + " 파일 삭제 오류 발생 또는 파일이 없음, DB에서 정보 삭제");
            //return true;
        }
        File folder = new File(savePath);

        boolean fileResult = fileDao.deleteById(fileVO.getId());

        FileUtil.deleteFolder(folder);
        folder.delete();

        boolean oleResult = oleDao.deleteById(fileVO.getId());

        return fileResult && oleResult;
    }
}