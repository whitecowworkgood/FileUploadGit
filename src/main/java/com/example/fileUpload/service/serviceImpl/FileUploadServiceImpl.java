package com.example.fileUpload.service.serviceImpl;


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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileUploadServiceImpl implements FileUploadService {
    private final SaveFileRepository saveFileRepository;

    @Value("${Save-Directory}")
    private String dir;

    private List<Map<String, String>> fileJson = new ArrayList<>();
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
//                Map<String, String> files = new HashMap<>();
//                files.put("filename", filename);
//
//                fileJson.add(files);

            FileEntity fileEntity = new FileEntity();

            fileEntity.setFileName(filename);
            fileEntity.setFileType(file.getContentType());
            fileEntity.setFileSize(file.getSize());

            saveFileRepository.save(fileEntity);

        }
    }

    @Override
    public List<Map<String, String>> printAll() {
        return fileJson;
    }

    @Override
    public Map<String, String> printOne(int id) {

        return fileJson.get(id);
    }
}