package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.ExtractEngine.DirectoryNodeParserAdapter;
import com.example.fileUpload.documentParser.parsers.abstracts.DocumentParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

@Slf4j
public class XWordParser extends DocumentParser {


    private final String fileSavePath;
    private final String oleSavePath;
    private final String originalFileName;

    private POIXMLDocument poixmlDocument;

    public XWordParser(String fileSavePath, String oleSavePath, String originalFileName) {
        this.fileSavePath = fileSavePath;
        this.oleSavePath = oleSavePath;
        this.originalFileName = originalFileName;
    }

    @Override
    public void extractEmbeddedObjects() {
        try (FileInputStream fs = new FileInputStream(this.fileSavePath);
             BufferedInputStream bi = new BufferedInputStream(fs)) {

            this.poixmlDocument = new XWPFDocument(bi);
            callDirectoryNodeParser();

        } catch (IOException |OpenXML4JException e) {
            ExceptionUtils.getStackTrace(e);

        }finally {
            IOUtils.closeQuietly(this.poixmlDocument);
        }
    }

    @SneakyThrows
    private void callDirectoryNodeParser() throws IOException, OpenXML4JException {

        List<PackagePart> xwpfDocumentAllEmbeddedParts = poixmlDocument.getAllEmbeddedParts();

        if(xwpfDocumentAllEmbeddedParts.isEmpty()){
            return;
        }

        generateFolder(this.oleSavePath);

        for(PackagePart hssfObjectData : xwpfDocumentAllEmbeddedParts) {
            System.out.println(hssfObjectData.getPartName().getName());
            DirectoryNodeParserAdapter directoryNodeParserAdapter = new DirectoryNodeParserAdapter(hssfObjectData);
            directoryNodeParserAdapter.getEmbeddedFile(oleSavePath, originalFileName);

        }

    }

}
