package com.example.fileUpload.service;

import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.model.FileVO;
import com.example.fileUpload.model.OleDto;

import java.util.List;

public interface FileUploadService {

    boolean  fileUpload(FileDto fileDto);

    List<FileVO> printFileAll();

    FileVO printFileOne(Long id);

    List<OleDto> printOleAll(Long id);

    boolean deleteOne(Long id);
}
