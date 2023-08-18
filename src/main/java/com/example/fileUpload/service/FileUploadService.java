package com.example.fileUpload.service;

import com.example.fileUpload.dto.FileDto;

import java.util.List;

public interface FileUploadService {

    boolean fileUpload(FileDto fileDto);

    public List<FileDto> printAll();

    public FileDto printOne(Long id);

    boolean deleteOne(Long id);

    //void Scheduler();
}
