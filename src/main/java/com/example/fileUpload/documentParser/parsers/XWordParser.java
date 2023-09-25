package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OleExtractor.OleExtractorFactory;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;

import java.io.*;

@NoArgsConstructor
@Slf4j
public class XWordParser extends OleExtractor {
    FileInputStream fs = null;
    XWPFDocument docx = null;
    //XOfficeEntryHandler xOfficeEntryHandler = new XOfficeEntryHandler();

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException, OpenXML4JException {

        try{
            callOfficeHandler(fileDto);

        }catch (Exception e){
            catchException(e);

        } finally {
            closeResources();
        }
    }

    @Override
    protected void callOfficeHandler(FileDto fileDto) throws Exception {
        fs = new FileInputStream(fileDto.getFileSavePath());
        docx = new XWPFDocument(OPCPackage.open(fs));

        for (PackagePart pPart : docx.getAllEmbeddedParts())
            new OleExtractorFactory().createOleExtractor(pPart, fileDto);

    }

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(fs);
        IOUtils.closeQuietly(docx);
    }
}
