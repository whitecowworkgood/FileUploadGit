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
    private String fileName;

    @Column(nullable = false, length = 1020)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    public FileEntity() {

    }
}
