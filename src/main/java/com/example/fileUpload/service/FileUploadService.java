package com.example.fileUpload.service;

import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.Ole.OleVO;


import java.util.List;

public interface FileUploadService {

    void  fileUpload(FileDto fileDto);

    FileVO printFileOne(Long id);

    List<OleVO> printOleAll(Long id);

}
