package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OleExtractor.OleExtractorFactory;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

@NoArgsConstructor
@Slf4j
public class XWordParser extends OleExtractor {


    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException, OpenXML4JException {

        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        POIXMLDocument docx = null;

        try{


            fileInputStream = new FileInputStream(fileDto.getFileTempPath());
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            docx = new XWPFDocument(bufferedInputStream);

            List<PackagePart> docxList = docx.getAllEmbeddedParts();

            if(!docxList.isEmpty()){
                generateFolder(fileDto.getFileOlePath());

                for (PackagePart pPart : docxList){
                    new OleExtractorFactory().createModernOleExtractor(pPart, fileDto);
                }

            }

        }catch (Exception e){
            ExceptionUtils.getStackTrace(e);

        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(docx);
            IOUtils.closeQuietly(bufferedInputStream);
        }
    }


}
