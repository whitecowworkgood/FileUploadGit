package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.XOfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

@NoArgsConstructor
public class XExcelParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws IOException, OpenXML4JException {
        FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());

        XSSFWorkbook xlsx = new XSSFWorkbook(OPCPackage.open(fs));

        XOfficeEntryHandler.getParseFile(xlsx.getAllEmbeddedParts(), fileDto.getFileOlePath());

        xlsx.close();

    }
}
