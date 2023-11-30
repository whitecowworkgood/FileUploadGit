package com.example.fileUpload.documentParser.model.Impl;

import com.example.fileUpload.documentParser.model.DocumentFile;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.DirectoryNode;

import java.util.List;

public class XPowerPointDocumentFile implements DocumentFile {

    private String uuidFileName;
    private List<PackagePart> OLEInfo;
    private String savePath;

    @Override
    public void doExtract() {

    }
}
