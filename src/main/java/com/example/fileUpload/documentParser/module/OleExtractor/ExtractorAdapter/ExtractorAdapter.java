package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorAdapter;

import org.apache.poi.openxml4j.opc.PackagePart;

import java.io.IOException;
import java.io.InputStream;

public interface ExtractorAdapter {
    static PackagePart changeVersion(InputStream inputStream) throws IOException {
        return null;
    }
}
