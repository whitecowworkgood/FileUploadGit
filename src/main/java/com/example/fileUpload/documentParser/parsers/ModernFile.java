package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OleExtractor.OleExtractorFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

public class ModernFile implements File{

    private List<PackagePart> packagePartList;

    public ModernFile(String fileSavePath){

        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        POIXMLDocument poixmlDocument = null;

        try{

            fileInputStream = new FileInputStream(fileSavePath);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            poixmlDocument = new XWPFDocument(bufferedInputStream);

            this.packagePartList = poixmlDocument.getAllEmbeddedParts();

        }catch (Exception e){
            ExceptionUtils.getStackTrace(e);

        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(poixmlDocument);
            IOUtils.closeQuietly(bufferedInputStream);
        }
    }

    public List<PackagePart> getPackagePartList() {
        return packagePartList;
    }
}
