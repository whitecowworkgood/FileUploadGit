package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.*;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.util.MimeType;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.fileUpload.util.MimeType.PPT;

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
            case "application/octet-stream" -> {
<<<<<<< HEAD
                if (!fileDto.getOriginFileName().endsWith(".hwp")) {
                    throw new IllegalArgumentException();
=======
                if (fileName.endsWith(".hwp")) {
                    return new HwpParser();
>>>>>>> 0ab5e59af0fcc1dd27fd8a1fd530399b86c43cc9
                }
                return new HwpParser();
            }
            case "application/zip" -> {
                return new ZipParser();
            }
            default -> throw new IllegalArgumentException("Unsupported MIME type");
        }
    }
}
