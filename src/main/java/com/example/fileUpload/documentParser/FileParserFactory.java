package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.*;
import com.example.fileUpload.documentParser.parsers.abstracts.DocumentParser;
import com.example.fileUpload.model.File.FileDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Slf4j
@Component
public class FileParserFactory {

    public DocumentParser createParser(FileDto fileDto) {

        switch (fileDto.getFileType()) {

            case "application/vnd.ms-powerpoint" -> {
                return new PowerPointParser(fileDto.getFileTempPath(), fileDto.getFileOlePath(), fileDto.getOriginFileName());
            }
            case "application/vnd.ms-excel" -> {
                return new ExcelParser(fileDto.getFileTempPath(), fileDto.getFileOlePath(), fileDto.getOriginFileName());
            }
            case "application/msword" -> {
                return new WordParser(fileDto.getFileTempPath(), fileDto.getFileOlePath(), fileDto.getOriginFileName());
            }
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document"-> {
                return new XWordParser(fileDto.getFileTempPath(), fileDto.getFileOlePath(), fileDto.getOriginFileName());
            }
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> {
                return new XPowerPointParser(fileDto.getFileTempPath(), fileDto.getFileOlePath(), fileDto.getOriginFileName());
            }
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> {
                return new XExcelParser(fileDto.getFileTempPath(), fileDto.getFileOlePath(), fileDto.getOriginFileName());
            }
            case "application/x-hwp-v5" -> {
                return new HwpParser(fileDto.getFileTempPath(), fileDto.getFileOlePath(), fileDto.getOriginFileName());
            }
            default -> {
                return new OtherParser();

            }

        }
    }

}
