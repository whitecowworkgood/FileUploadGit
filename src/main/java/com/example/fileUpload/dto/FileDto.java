package com.example.fileUpload.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class FileDto {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileSavePath;
    private String fileOlePath;
    private MultipartFile fileData;

    //private OleDto oleDto;


    public FileDto() {
    }
}
