package com.example.fileUpload.model.File;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileVO {

    private Long id;
    private String UUIDFileName;
    @JsonIgnore
    private String fileOlePath;
    @JsonIgnore
    private String fileSavePath;
    private Long fileSize;
    private String originalFileName;
    private String fileType;
    private Long countNum;
    private String userName;
    private String comment;
    private String timeStamp;
    @JsonIgnore
    private boolean isEncrypt;
}
