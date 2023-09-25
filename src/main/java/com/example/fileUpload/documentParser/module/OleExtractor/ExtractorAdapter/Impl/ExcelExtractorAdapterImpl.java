package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorAdapter.Impl;

import com.example.fileUpload.documentParser.module.OleExtractor.ExtractorAdapter.ExtractorAdapter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;


@NoArgsConstructor
public class ExcelExtractorAdapterImpl implements ExtractorAdapter {

    public static PackagePart changeVersion(InputStream inputStream) throws IOException {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            return xssfWorkbook.getAllEmbeddedParts();
        } catch (IOException e) {
            // 예외 처리
            ExceptionUtils.getStackTrace(e);
            throw e;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}