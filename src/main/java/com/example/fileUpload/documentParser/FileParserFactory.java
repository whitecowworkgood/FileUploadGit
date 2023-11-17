package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.*;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Slf4j
@Component
public class FileParserFactory {

    public OleExtractor createParser(FileDto fileDto) {

        switch (fileDto.getFileType()) {

            case "application/vnd.ms-powerpoint" -> {
                return new PowerPointParser();
            }
            case "application/vnd.ms-excel" -> {
                return new ExcelParser();
            }
            case "application/msword" -> {
                return new WordParser();
            }
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {
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
            case"application/haansoftdocx" ->{
                return new XWordParser();
            }
            case "application/octet-stream" -> {

                if (!fileDto.getOriginFileName().endsWith(".hwp")) {
                    throw new IllegalArgumentException();

                }
                return new HwpParser();
            }
            case "application/zip" -> {
                return new ZipParser();
            }
            default -> {
                return new OtherParser();
            }

        }
    }

}
