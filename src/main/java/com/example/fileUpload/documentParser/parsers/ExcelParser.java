package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


import java.io.FileInputStream;
import java.io.IOException;

@NoArgsConstructor
public class ExcelParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws IOException {
        FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
        for (HSSFObjectData hssfObjectData : hssfWorkbook.getAllEmbeddedObjects()) {

            OfficeEntryHandler.getParser(hssfObjectData.getDirectory(), fileDto.getFileOlePath());

        }
        hssfWorkbook.close();

    }
}
