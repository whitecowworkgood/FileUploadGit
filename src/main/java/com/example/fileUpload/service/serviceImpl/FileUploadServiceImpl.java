package com.example.fileUpload.service.serviceImpl;


import com.example.fileUpload.dto.FileDto;
import com.example.fileUpload.entity.FileEntity;
import com.example.fileUpload.repository.SaveFileRepository;
import com.example.fileUpload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileUploadServiceImpl implements FileUploadService {

    private final SaveFileRepository saveFileRepository;
    private final ModelMapper modelMapper;

    @Value("${Save-Directory}")
    private String dir;

    //추후 프로젝트 경로나, c:\\경로에 폴더가 있는지 확인 후, 없으면 폴더 생성 후 파일 전송하기

    @Override
    public boolean fileUpload(FileDto fileDto) {

            MultipartFile file = fileDto.getFileData();

            try {
                String fullPath = dir + fileDto.getFileName();
                file.transferTo(new File(fullPath));

                FileEntity fileEntity = modelMapper.map(fileDto, FileEntity.class);
                saveFileRepository.save(fileEntity);
                return true;

            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류 발생"+e);
            }

    }

    @Override
    public List<FileDto> printAll() {
        //return saveFileRepository.findAll();
        List<FileEntity> fileEntities = saveFileRepository.findAll();
        List<FileDto> fileDtos = new ArrayList<>();

        for (FileEntity fileEntity : fileEntities) {
            FileDto fileDto = modelMapper.map(fileEntity, FileDto.class);
            fileDtos.add(fileDto);
        }

        return fileDtos;
    }

    @Override
    public FileDto printOne(Long id) {
        Optional<FileEntity> optionalFileEntity = saveFileRepository.findById(id);
        FileEntity fileEntity = optionalFileEntity.orElse(null);

        if (fileEntity != null) {
            return modelMapper.map(fileEntity, FileDto.class);
        } else {
            return null; // 또는 예외 처리 등
        }
    }

    @Override
    public boolean deleteOne(Long id) {

        FileEntity fileEntity = saveFileRepository.findById(id).orElse(null);

        if(fileEntity != null){
            String fileName = fileEntity.getFileName();
            File file = new File(dir+fileName);

            if(file.exists()){

                if(file.delete()){
                    saveFileRepository.deleteById(fileEntity.getId());
                }else{
                    log.warn(fileName+"파일 삭제 오류 발생");

                }
            }else{
                log.warn(fileName+"파일이 없음, DB에서 정보 삭제");
                saveFileRepository.deleteById(fileEntity.getId());
            }
            return true;

        }else{
            return false;
        }

    }
}