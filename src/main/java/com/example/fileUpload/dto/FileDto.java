package com.example.fileUpload.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class FileDto {

    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;

    //private OleDto oleDto;


}
