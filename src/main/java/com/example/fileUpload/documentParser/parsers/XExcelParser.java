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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

@NoArgsConstructor
public class XExcelParser extends OleExtractor {


    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException, OpenXML4JException {
        FileInputStream fileInputStream = null;
        POIXMLDocument xlsx = null;
        BufferedInputStream bufferedInputStream = null;

        try{

            fileInputStream = new FileInputStream(fileDto.getFileTempPath());
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            xlsx = new XSSFWorkbook(bufferedInputStream);

            List<PackagePart> xlsxList = xlsx.getAllEmbeddedParts();

            if(!xlsxList.isEmpty()){
                generateFolder(fileDto.getFileOlePath());

                for (PackagePart pPart : xlsxList){
                    new OleExtractorFactory().createModernOleExtractor(pPart, fileDto);
                }

            }

        }catch (Exception e){
            ExceptionUtils.getStackTrace(e);

        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(xlsx);
            IOUtils.closeQuietly(bufferedInputStream);
        }
    }

}
