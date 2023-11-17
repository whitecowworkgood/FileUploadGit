package com.example.fileUpload.message;

import lombok.Data;

import java.util.List;

@Data
public class TestMessage {
    private String status;
    private List<String> files;
}
