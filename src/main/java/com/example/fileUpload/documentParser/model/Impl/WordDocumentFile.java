package com.example.fileUpload.documentParser.model.Impl;

import com.example.fileUpload.documentParser.model.DocumentFile;
import org.apache.poi.poifs.filesystem.DirectoryNode;

import java.util.List;

public class WordDocumentFile implements DocumentFile {

    private String uuidFileName;
    private List<DirectoryNode> OLEInfo;
    private String savePath;


    @Override
    public void doExtract() {

    }
}
