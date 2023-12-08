package com.example.fileUpload.model.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FileDto{
    private Long id;
    private String UUIDFileName;
    private String fileOlePath;
    private String fileSavePath;
    private Long fileSize;
    private String fileType;
    private String originFileName;
    private String fileTempPath;
    private Long countNum;
    private String userName;
    private String comment;

}