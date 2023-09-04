package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.XOfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class XWordParser extends FileParser {
    private final XOfficeEntryHandler xOfficeEntryHandler;

    @Override
    public void parse(FileDto fileDto) throws IOException, OpenXML4JException {
        FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());
        XWPFDocument docx = new XWPFDocument(fs);

        xOfficeEntryHandler.getParseFile(docx.getAllEmbeddedParts(), fileDto.getFileOlePath());
        docx.close();

    }

}
