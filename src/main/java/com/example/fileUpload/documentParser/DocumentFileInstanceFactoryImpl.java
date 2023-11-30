package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.model.DocumentFile;
import com.example.fileUpload.model.File.FileDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;

@Slf4j
@Component
public class DocumentFileInstanceFactoryImpl implements DocumentFileInstanceFactory{

    @Value("${Temp-Diractory}")
    private String tempDir;

    public DocumentFile createDocumentFileInstance(FileDto fileDto){
        try {
            FileInputStream fileInputStream = new FileInputStream(String.format("%s/%s", tempDir, fileDto.getUUIDFileName()));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        switch (fileDto.getFileType()){

            //TODO: 나중에 OLE 객체 정보를 저장하는 기능 구현하기
            case "application/vnd.ms-powerpoint" -> {

            }
            case "application/vnd.ms-excel" -> {

            }
            case "application/msword" -> {

            }
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/haansoftdocx" -> {

            }
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> {

            }
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> {

            }
            case"application/haansofthwp" ->{

            }
            case "application/octet-stream" -> {
                if (!fileDto.getOriginFileName().endsWith(".hwp")) {
                    throw new IllegalArgumentException();
                }
               //
            }
            case "application/zip" -> {

            }
            default -> {
                log.info("아직 기타 경우는 생각 안함.");
                throw new RuntimeException("아직 구현 안함");
            }
        }
        return null;
    }

    /*private static DocumentType detectDocumentType(String fileName) {
        if (fileName.toLowerCase().endsWith(".docx") || fileName.toLowerCase().endsWith(".pptx") || fileName.toLowerCase().endsWith(".xlsx")) {
            return DocumentType.MODERN;

        } else if (fileName.toLowerCase().endsWith(".doc") || fileName.toLowerCase().endsWith(".ppt") || fileName.toLowerCase().endsWith(".xls")
                ||  fileName.toLowerCase().endsWith(".hwp")) {
            return DocumentType.LEGACY;
        }
        return DocumentType.UNKNOWN;
    }*/

}
