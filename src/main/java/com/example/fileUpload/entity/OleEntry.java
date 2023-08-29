package com.example.fileUpload.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
public class OleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long superId;

    @Column(nullable = false, length = 1020)
    private String fileName;


    public OleEntry() {

    }

}
