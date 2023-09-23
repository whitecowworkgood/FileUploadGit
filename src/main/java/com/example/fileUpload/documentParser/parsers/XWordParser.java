package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.XOfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;

import java.io.*;

@NoArgsConstructor
@Slf4j
public class XWordParser extends OleExtractor {

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException, OpenXML4JException {

        FileInputStream fs = null;
        XWPFDocument docx = null;
        XOfficeEntryHandler xOfficeEntryHandler = new XOfficeEntryHandler();

        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            docx = new XWPFDocument(OPCPackage.open(fs));

            for (PackagePart pPart : docx.getAllEmbeddedParts()) {

                //잠시 주석처리
                xOfficeEntryHandler.parser(pPart, fileDto.getOriginFileName(), fileDto.getFileOlePath());
            }

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        } catch (XmlException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(docx);
        }
    }
}
