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
    public FileParser createParser(String mimeType, String fileName){
        log.info(mimeType);
        switch (mimeType){
            case "application/vnd.ms-powerpoint"->{
                return new PowerPointParser();
            }
            case "application/vnd.ms-excel"->{
                return new ExcelParser();
            }
            case "application/msword"->{
                return new WordParser();
            }
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document"->{
                return new XWordParser();
            }
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation"->{
                return new XPowerPointParser();
            }
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ->{
                return new XExcelParser();
            }
            case "application/octet-stream" -> {
                if (fileName.equals(".hwp")) {
                    return new HwpParser();
                    // return new XHwpParser();
                }
                //return new HwpParser();
                return new XHwpParser();
            }
            default -> {
                throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
            }
        }
    }
}
