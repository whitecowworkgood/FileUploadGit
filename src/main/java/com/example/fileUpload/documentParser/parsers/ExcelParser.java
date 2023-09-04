package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ExcelParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws IOException {
        //InputStream fs = new InputStream(fileDto.getFileSavePath());
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fileDto.getFileData().getInputStream());
        for (HSSFObjectData hssfObjectData : hssfWorkbook.getAllEmbeddedObjects()) {

            OfficeEntryHandler.getParser(hssfObjectData.getDirectory(), fileDto.getFileOlePath());

        }
        hssfWorkbook.close();

    }
}
