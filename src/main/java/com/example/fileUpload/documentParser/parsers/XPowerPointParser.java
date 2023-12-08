package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OleExtractor.OleExtractorFactory;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.xmlbeans.XmlException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

@NoArgsConstructor
public class XPowerPointParser extends OleExtractor {


    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws OpenXML4JException, IOException, XmlException {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        POIXMLDocument pptx = null;

        try{

            fileInputStream = new FileInputStream(fileDto.getFileTempPath());
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            pptx = new XMLSlideShow(bufferedInputStream);

            List<PackagePart> pptxList = pptx.getAllEmbeddedParts();

            if(!pptxList.isEmpty()){
                generateFolder(fileDto.getFileOlePath());

                for (PackagePart pPart : pptxList){
                    new OleExtractorFactory().createModernOleExtractor(pPart, fileDto);
                }

            }

        }catch (Exception e){
            ExceptionUtils.getStackTrace(e);

        }finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(pptx);
            IOUtils.closeQuietly(bufferedInputStream);
        }
    }

}
