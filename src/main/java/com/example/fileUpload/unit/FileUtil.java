package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.xmlbeans.XmlCursor;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.xml.namespace.QName;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.xmlbeans.XmlCursor;
import org.xml.sax.SAXException;


import javax.xml.namespace.QName;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class FileUtil {

    public static boolean valuedDocFile(FileDto fileDto){
        List<String> validTypeList = List.of("application/pdf","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "application/msword");

        return validTypeList.contains(fileDto.getFileType());
    }

    public static boolean isValidPath(String defaultPath, String savePath){
        if (defaultPath == null || savePath == null || defaultPath.isEmpty() || savePath.isEmpty()) {
            return false;
        }

        if (!savePath.startsWith(defaultPath)) {
            return false;
        }

        File saveFile = new File(savePath);
        File defaultDir = new File(defaultPath);

        try {

            String normalizedSavePath = saveFile.getCanonicalPath();
            String normalizedDefaultPath = defaultDir.getCanonicalPath();

            return normalizedSavePath.startsWith(normalizedDefaultPath);
        } catch (IOException e) {

            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    public static void fileOleParser(String pathFile) {
        //List<String> oleObjectList = new ArrayList<>(); // OLE 정보를 담을 리스트

        //File documentFile = new File(pathFile);

        /*try (InputStream stream = new FileInputStream(documentFile)) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();

            parser.parse(stream, handler, metadata, context);

            String[] oleObjectInfo = metadata.getValues("OLE-Object-Info");
            if (oleObjectInfo != null && oleObjectInfo.length > 0) {
                System.out.println("OLE Object Information: " + oleObjectInfo[0]);

            }
            //return oleObjectInfo;
        } catch (TikaException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }*/

        /*try (FileInputStream fis = new FileInputStream(pathFile)) {
            XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(fis));
            List<XWPFPicture> pictures = document.getAllPictures();

            for (XWPFPicture picture : pictures) {
                XmlCursor cursor = picture.getCTPicture().newCursor();
                cursor.selectPath("./*");

                while(cursor.toNextSelection()) {
                    if (cursor.getName().getLocalPart().equals("imagedata")) {
                        String oleFilename = cursor.getAttributeText(new QName("ole", "filename"));
                        System.out.println("OLE Object Filename: " + oleFilename);
                    }
                }
            }
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
        }*/

    }

    public static List<String> getFolderFiles(String folderPath){
        File folder = new File(folderPath);
        String[] fileList = folder.list();
        return List.of(fileList);
    }

    private FileUtil() {
    }
}

