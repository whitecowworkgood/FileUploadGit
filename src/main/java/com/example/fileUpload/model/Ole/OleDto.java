package com.example.fileUpload.model.Ole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OleDto{
    private Long id;
    private String UUIDFileName;
    private String originalFileName;
    private Long superId;
}