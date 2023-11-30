package com.example.fileUpload.model.DTOFactory;

import com.example.fileUpload.model.File.FileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileDTOFactory {
    FileDto generateUploadDtoOf(MultipartFile multipartFile, Long countNum, String comment) throws IOException;
}
