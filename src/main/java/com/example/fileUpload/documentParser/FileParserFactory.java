package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.*;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Slf4j
@Component
public class FileParserFactory {

    public OleExtractor createParser(String mimeType, String uuidFileName) {


        switch (mimeType) {

            case "application/vnd.ms-powerpoint" -> {
                return new PowerPointParser();
            }
            case "application/vnd.ms-excel" -> {
                return new ExcelParser();
            }
            case "application/msword" -> {
                return new WordParser();
            }
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/haansoftdocx" -> {
                return new XWordParser();
            }
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> {
                return new XPowerPointParser();
            }
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> {
                return new XExcelParser();
            }
            case"application/haansofthwp" ->{
                return new HwpParser();
            }
            case "application/octet-stream" -> {

                if (!uuidFileName.endsWith(".hwp")) {
                    throw new IllegalArgumentException();

                }
                return new HwpParser();
            }
            default -> {
                return new OtherParser();
            }

        }
    }

}
