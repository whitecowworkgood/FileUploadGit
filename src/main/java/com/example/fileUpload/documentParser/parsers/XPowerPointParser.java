package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OleExtractor.OleExtractorFactory;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.xmlbeans.XmlException;

import java.io.FileInputStream;
import java.io.IOException;

@NoArgsConstructor
public class XPowerPointParser extends OleExtractor {

    FileInputStream fs = null;
    XMLSlideShow pptx = null;

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws OpenXML4JException, IOException, XmlException {


        try{
            callOfficeHandler(fileDto);

        }catch (Exception e){
            catchException(e);

        }finally {
            closeResources();
        }
    }

    @Override
    protected void callOfficeHandler(FileDto fileDto) throws Exception {
        fs = new FileInputStream(fileDto.getFileSavePath());
        pptx = new XMLSlideShow(OPCPackage.open(fs));

        for (PackagePart pPart : pptx.getAllEmbeddedParts())
            new OleExtractorFactory().createOleExtractor(pPart, fileDto);

    }

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(fs);
        IOUtils.closeQuietly(pptx);
    }
}
