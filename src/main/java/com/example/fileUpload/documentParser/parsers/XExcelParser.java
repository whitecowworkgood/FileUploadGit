package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.ExtractEngine.DirectoryNodeParserAdapter;
import com.example.fileUpload.documentParser.parsers.abstracts.DocumentParser;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;


public class XExcelParser extends DocumentParser {
    private final String fileSavePath;
    private final String oleSavePath;
    private final String originalFileName;

    private XSSFWorkbook xssfWorkbook;

    public XExcelParser(String fileSavePath, String oleSavePath, String originalFileName) {
        this.fileSavePath = fileSavePath;
        this.oleSavePath = oleSavePath;
        this.originalFileName = originalFileName;
    }

    @Override
    public void extractEmbeddedObjects() {
        try (FileInputStream fs = new FileInputStream(this.fileSavePath);
             BufferedInputStream bi = new BufferedInputStream(fs)) {

            this.xssfWorkbook = new XSSFWorkbook(bi);
            callDirectoryNodeParser();

        } catch (IOException |OpenXML4JException e) {
            ExceptionUtils.getStackTrace(e);

        }finally {
            IOUtils.closeQuietly(this.xssfWorkbook);
        }
    }

    @SneakyThrows
    private void callDirectoryNodeParser() throws IOException, OpenXML4JException {
        List<PackagePart> xssfWorkbookAllEmbeddedParts = xssfWorkbook.getAllEmbeddedParts();

        if(xssfWorkbookAllEmbeddedParts.isEmpty()){
            return;
        }

        generateFolder(this.oleSavePath);

        for(PackagePart hssfObjectData : xssfWorkbookAllEmbeddedParts) {

            DirectoryNodeParserAdapter directoryNodeParserAdapter = new DirectoryNodeParserAdapter(hssfObjectData);
            directoryNodeParserAdapter.getEmbeddedFile(oleSavePath, originalFileName);

        }

    }

}
