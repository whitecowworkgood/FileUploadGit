package com.example.fileUpload.model.File;

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

}
