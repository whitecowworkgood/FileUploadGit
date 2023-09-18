package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.XOfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlException;

import java.io.FileInputStream;
import java.io.IOException;

@NoArgsConstructor
public class XExcelParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws IOException, OpenXML4JException {

        FileInputStream fs = null;
        XSSFWorkbook xlsx = null;
        XOfficeEntryHandler xOfficeEntryHandler = new XOfficeEntryHandler();
        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            xlsx = new XSSFWorkbook(OPCPackage.open(fs));

            for (PackagePart pPart : xlsx.getAllEmbeddedParts()) {
                xOfficeEntryHandler.parser(pPart, fileDto.getOriginFileName(), fileDto.getFileOlePath());
            }

        }catch (IOException | XmlException e){
            ExceptionUtils.getStackTrace(e);
        }finally {
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(xlsx);
        }
    }
}
