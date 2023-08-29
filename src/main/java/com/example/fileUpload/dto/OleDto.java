package com.example.fileUpload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OleDto {
    private Long id;
    private Long superId;
    private String fileName;

    public OleDto() {
    }
}
