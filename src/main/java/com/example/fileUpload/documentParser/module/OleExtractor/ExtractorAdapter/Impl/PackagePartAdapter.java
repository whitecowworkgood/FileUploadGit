package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorAdapter.Impl;

import com.example.fileUpload.documentParser.module.OleExtractor.ExtractorAdapter.ExtractorAdapter;
import com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory.ExcelExtractor;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;

public class PackagePartAdapter implements ExtractorAdapter {
    private final POIXMLTextExtractor extractor;

    public PackagePartAdapter(POIXMLTextExtractor extractor) {
        this.extractor = extractor;
    }


    public byte[] getDocumentBytes() {
        // 2003 이후 버전에서 PackagePart를 이용해 문서 추출
        return extractor.getText().getBytes();
    }


}