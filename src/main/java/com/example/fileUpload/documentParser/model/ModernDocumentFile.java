package com.example.fileUpload.documentParser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModernDocumentFile implements DocumentFile {
    private String uuidFileName;
    private List<String> OLEInfo;
    private String savePath;


    public void doExtract(){

    }
}
