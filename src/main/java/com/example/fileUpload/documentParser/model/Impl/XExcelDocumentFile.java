package com.example.fileUpload.documentParser.model.Impl;

import com.example.fileUpload.documentParser.model.DocumentFile;
import org.apache.poi.openxml4j.opc.PackagePart;

import java.util.List;

public class XExcelDocumentFile implements DocumentFile {

    private String uuidFileName;
    private List<PackagePart> OLEInfo;
    private String savePath;

    @Override
    public void doExtract() {

    }
}
