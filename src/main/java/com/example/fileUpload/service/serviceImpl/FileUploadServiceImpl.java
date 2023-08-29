package com.example.fileUpload.service.serviceImpl;


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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
@SuppressWarnings("ResultOfMethodCallIgnored")
//@SuppressWarnings("unused")
public class FileUploadServiceImpl implements FileUploadService {
    private final SaveFileRepository saveFileRepository;
    private final SaveOleRepository saveOleRepository;
    private final ModelMapper modelMapper;

    @Value("${Save-Directory}")
    private String dir;
    //추후 프로젝트 경로나, c:\\경로에 폴더가 있는지 확인 후, 없으면 폴더 생성 후 파일 전송하기

    @Override
    @Transactional
    public boolean fileUpload(FileDto fileDto) {

        try {
            if (!fileDto.getFileData().isEmpty()) {

                if (FileUtil.isValidPath(dir, fileDto.getFileSavePath())) {

                    if (FileUtil.valuedDocFile(fileDto)) {

                        fileDto.getFileData().transferTo(new File(fileDto.getFileSavePath()));
                        FileEntity fileEntity = modelMapper.map(fileDto, FileEntity.class);

                        FileEntity savedFileEntity =saveFileRepository.save(fileEntity);

                        List<String> fileList = FileUtil.getOleFiles(fileDto);

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
    public OleDto printOleOne(Long id) {
/*        List<OleEntry> OleDtos = saveOleRepository.findBySuperId(id);
        //log.info(optionalOleDtoEntity.toString());

        return OleDtos.stream().map(OleEntity ->
                        modelMapper.map(OleEntity, OleDto.class))

        return OleDtos.stream()
                .map(fileEntity -> modelMapper.map(fileEntity, FileDto.class))
                .collect(Collectors.toList());*/

    }

    @Override
    public boolean deleteOne(Long id) {

        FileEntity fileEntity = saveFileRepository.findById(id).orElse(null);
        if (fileEntity == null) {
            return false;
        }

        String fileName = fileEntity.getFileName();
        String fullPath = dir + fileName;

        if (!FileUtil.isValidPath(dir, fullPath)) {
            return false;
        }

        File file = new File(fullPath);

        if (!(file.exists() && file.delete())) {
            log.warn(fileName + " 파일 삭제 오류 발생 또는 파일이 없음, DB에서 정보 삭제");
            //return true;
        }
        saveFileRepository.deleteById(fileEntity.getId());
        return true;
    }
}