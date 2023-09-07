package com.example.fileUpload.service.serviceImpl;


import com.example.fileUpload.documentParser.FileProcessor;
import com.example.fileUpload.dto.FileDto;
import com.example.fileUpload.dto.OleDto;
import com.example.fileUpload.entity.FileEntity;
import com.example.fileUpload.entity.OleEntry;
import com.example.fileUpload.repository.SaveFileRepository;
import com.example.fileUpload.repository.SaveOleRepository;
import com.example.fileUpload.service.FileUploadService;
import com.example.fileUpload.unit.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.fileUpload.unit.FileUtil.folderSearch;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileUploadServiceImpl implements FileUploadService {
    private final SaveFileRepository saveFileRepository;
    private final SaveOleRepository saveOleRepository;
    private final ModelMapper modelMapper;
    private final FileProcessor fileProcessor;

    @Value("${Save-Directory}")
    private String dir;
    //추후 프로젝트 경로나, c:\\경로에 폴더가 있는지 확인 후, 없으면 폴더 생성 후 파일 전송하기

    @Override
    @Transactional
    public boolean fileUpload(FileDto fileDto) {

        try {
            if (!fileDto.getFileData().isEmpty()) {

                if (FileUtil.isPathValidForStorage(dir, fileDto.getFileSavePath())) {

                    if (FileUtil.validateUploadedFileMimeType(fileDto)) {

                        fileDto.getFileData().transferTo(new File(fileDto.getFileSavePath()));
                        FileEntity fileEntity = modelMapper.map(fileDto, FileEntity.class);


                        FileEntity savedFileEntity =saveFileRepository.save(fileEntity);

                        //File Folder = new File(fileDto.getFileOlePath());
                        //;
                        if(!Files.exists(Path.of(fileDto.getFileOlePath()))){
                            try{
                                //Folder.mkdir(); //폴더 생성합니다.
                                Files.createDirectories(Path.of(fileDto.getFileOlePath()));
                            }
                            catch(Exception e){
                                ExceptionUtils.getStackTrace(e);
                            }
                        }


                        fileProcessor.processFiles(fileDto);

                        List<String> fileList = folderSearch(fileDto.getFileOlePath());

                        //기존 코드
                        //List<String> fileList = FileUtil.processAndRetrieveFilesByType(fileDto);

                        for (String fileName : fileList) {
                            //log.info(fileName);
                            OleDto oleDto = OleDto.builder().superId(savedFileEntity.getId())
                                    .fileName(fileName)
                                    .build();

                            OleEntry oleEntity = modelMapper.map(oleDto, OleEntry.class);
                            saveOleRepository.save(oleEntity); // Ole 정보 저장
                        }

                        return true;
                    }
                }
            }
        } catch (RuntimeException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            new File(dir + fileDto.getFileName()).delete();

        } catch (IOException i) {
            log.error(ExceptionUtils.getStackTrace(i));
        }
        return false;
    }

    @Override
    public List<FileDto> printFileAll() {

        List<FileEntity> fileEntities = saveFileRepository.findAll();

        return fileEntities.stream()
                .map(fileEntity -> modelMapper.map(fileEntity, FileDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public FileDto printFileOne(Long id) {
        Optional<FileEntity> optionalFileEntity = saveFileRepository.findById(id);

        return optionalFileEntity.map(fileEntity -> modelMapper.map(fileEntity, FileDto.class))
                .orElse(null);
    }

    @Override
    public List<OleDto> printOleAll(Long id) {
        List<OleEntry> oleEntries = saveOleRepository.findBySuperId(id);

        return oleEntries.stream()
                .map(oleEntry -> modelMapper.map(oleEntry, OleDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteOne(Long id) {

        FileEntity fileEntity = saveFileRepository.findById(id).orElse(null);
        if (fileEntity == null) {
            return false;
        }

        String fileName = fileEntity.getFileName();
        String savePath = fileEntity.getFileOlePath();
        String fullPath = dir + fileName;

        if (!FileUtil.isPathValidForStorage(dir, fullPath)) {
            return false;
        }

        File file = new File(fullPath);

        if (!(file.exists() && file.delete())) {
            log.warn(fileName + " 파일 삭제 오류 발생 또는 파일이 없음, DB에서 정보 삭제");
            //return true;
        }
        File folder = new File(savePath);

        saveFileRepository.deleteById(fileEntity.getId());

        FileUtil.deleteFolder(folder);
        folder.delete();
        saveOleRepository.deleteAllBySuperId(fileEntity.getId());
        return true;
    }
}