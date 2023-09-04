package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.XOfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class XExcelParser extends FileParser {
    private final XOfficeEntryHandler xOfficeEntryHandler;
    @Override
    public void parse(FileDto fileDto) throws IOException, OpenXML4JException {
        FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());

        XSSFWorkbook xlsx = new XSSFWorkbook(OPCPackage.open(fs));

        xOfficeEntryHandler.getParseFile(xlsx.getAllEmbeddedParts(), fileDto.getFileOlePath());

        xlsx.close();

    }
}
