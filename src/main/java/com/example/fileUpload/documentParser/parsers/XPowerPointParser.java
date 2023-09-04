package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.XOfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class XPowerPointParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws OpenXML4JException, IOException, XmlException {
        FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());

        XSLFSlideShow pptx = new XSLFSlideShow(OPCPackage.open(fs));

        XOfficeEntryHandler.getParseFile(pptx.getAllEmbeddedParts(), fileDto.getFileOlePath());
        pptx.close();

    }
}
