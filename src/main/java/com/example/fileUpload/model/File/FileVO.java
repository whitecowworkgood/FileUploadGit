package com.example.fileUpload.model.File;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileVO {
    private Long id;
    private String UUIDFileName;
    private String fileOlePath;
    private String fileSavePath;
    private Long fileSize;
    private String fileType;
    private String originalFileName;
    private Long countNum;
    private String userName;
}
