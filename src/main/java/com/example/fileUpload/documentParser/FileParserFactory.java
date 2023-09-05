package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.*;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Slf4j
@Component
public class FileParserFactory {
    public FileParser createParser(String mimeType){
        switch (mimeType){
            case "application/vnd.ms-powerpoint"->{
               // log.info("ppt");
                return new PowerPointParser();
            }
            case "application/vnd.ms-excel"->{
                //log.info("xls");
                return new ExcelParser();
            }
            case "application/msword"->{
               // log.info("doc");
                return new WordParser();
            }
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document"->{
                //log.info("docx");
                return new XWordParser();
            }
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation"->{
               // log.info("pptx");
                return new XPowerPointParser();
            }
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ->{
                //log.info("xlsx");
                return new XExcelParser();
            }
            case "application/octet-stream" -> {
                //log.info("한컴");
                return new HwpParser();
            }
            default -> {
                //log.info("에러에러");
                throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
            }
        }
    }
}
