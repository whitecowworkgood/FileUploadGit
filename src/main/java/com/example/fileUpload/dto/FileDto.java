package com.example.fileUpload.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class FileDto {

    private String fileName;
    private String fileType;
    private Long fileSize;

    //private OleDto oleDto;

}
