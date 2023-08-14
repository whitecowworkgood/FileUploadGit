package com.example.fileUpload.service.serviceImpl;


import com.example.fileUpload.dto.FileDto;
import com.example.fileUpload.entity.FileEntity;
import com.example.fileUpload.repository.SaveFileRepository;
import com.example.fileUpload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileUploadServiceImpl implements FileUploadService {
    private final SaveFileRepository saveFileRepository;

    @Value("${Save-Directory}")
    private String dir;

    //추후 프로젝트 경로나, c:\\경로에 폴더가 있는지 확인 후, 없으면 폴더 생성 후 파일 전송하기

    @Override
    public void fileUpload(MultipartFile file) {

        if (!file.isEmpty()) {
            String filename = file.getOriginalFilename();
            //log.info("file.getOriginalFilename = {}", filename);

            String fullPath = dir + filename;

            try {
                file.transferTo(new File(fullPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            FileEntity fileEntity = new FileEntity();

            fileEntity.setFileName(filename);
            fileEntity.setFileType(file.getContentType());
            fileEntity.setFileSize(file.getSize());

            saveFileRepository.save(fileEntity);

        }
    }

    @Override
    public Object printAll() {
        return saveFileRepository.findAll();
    }

    @Override
    public FileEntity printOne(Long id) {
        //System.out.println(saveFileRepository.findById(id));

        Optional<FileEntity> optionalFileEntity = saveFileRepository.findById(id);
        return optionalFileEntity.orElse(null);
       // return saveFileRepository.findById(id);
    }

    @Override
    public void deleteOne(Long id) {

        FileEntity fileEntity = saveFileRepository.findById(id).get();


        String fileName = fileEntity.getFileName();

        File file = new File(dir+fileName);

        if(file.exists()){
            if(file.delete()){
                saveFileRepository.deleteById(id);
            }else{
                log.warn(fileName+"파일 삭제 오류 발생");

            }
        }else{
            log.warn(fileName+"파일이 없음, DB에서 정보 삭제");
            saveFileRepository.deleteById(id);
        }

    }
}