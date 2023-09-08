package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.XOfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.IOException;

@NoArgsConstructor
@Slf4j
public class XWordParser extends FileParser {


    @Override
    public void parse(FileDto fileDto) throws IOException, OpenXML4JException {
        FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());
        XWPFDocument docx = new XWPFDocument(fs);

        log.info(docx.getProperties().getExtendedProperties().getApplication());

        XOfficeEntryHandler.getParseFile(docx.getAllEmbeddedParts(), fileDto.getFileOlePath());
        docx.close();

    }

}
