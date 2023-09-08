package com.example.fileUpload.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
public class FileEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1020)
    private String UUIDFileName;

    @Column(nullable = false, length = 1020)
    private String originFileName;

    @Column(nullable = false, length = 1020)
    private String fileSavePath;

    @Column(nullable = false, length = 1020)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 1020)
    private String fileOlePath;

    public FileEntity() {

    }
}
