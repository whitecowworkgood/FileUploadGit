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
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.EMFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class XExcelParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws IOException, OpenXML4JException {

/*
        String oleObjectId = null;
        String objectPrId = null;
*/

        FileInputStream fs = null;
        XSSFWorkbook xlsx = null;
        XOfficeEntryHandler xOfficeEntryHandler = new XOfficeEntryHandler();
        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            xlsx = new XSSFWorkbook(OPCPackage.open(fs));

            for (PackagePart pPart : xlsx.getAllEmbeddedParts()) {
                xOfficeEntryHandler.parser(pPart, fileDto.getOriginFileName(), fileDto.getFileOlePath());
            }

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        } catch (XmlException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(xlsx);
        }
    }
}
