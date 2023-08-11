package com.example.fileUpload.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
public class File {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Builder
    public File(Long id, String fileName, String filePath, String fileType, Long fileSize) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}
