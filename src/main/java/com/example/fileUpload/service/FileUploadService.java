package com.example.fileUpload.service;

import com.example.fileUpload.dto.FileDto;
import com.example.fileUpload.dto.OleDto;

import java.util.List;

public interface FileUploadService {

    boolean fileUpload(FileDto fileDto);

    public List<FileDto> printFileAll();

    public FileDto printFileOne(Long id);

    public List<OleDto> printOleAll(Long id);

    boolean deleteOne(Long id);
}
