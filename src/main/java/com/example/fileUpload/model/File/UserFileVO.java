package com.example.fileUpload.model.File;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserFileVO {

    private Long fileSize;
    private String originalFileName;
    private Long countNum;
    private String comment;
    private String timeStamp;
    @JsonIgnore
    private boolean isEncrypt;

}
