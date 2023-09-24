package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.*;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.util.MimeType;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Slf4j
@Component
public class FileParserFactory {

    public OleExtractor createParser(FileDto fileDto) {
        switch (MimeType.valueOf(fileDto.getFileType())) {
            case PPT -> {
                return new PowerPointParser();
            }
            case XLS -> {
                return new ExcelParser();
            }
            case DOC -> {
                return new WordParser();
            }
            case DOCX -> {
                return new XWordParser();
            }
            case PPTX -> {
                return new XPowerPointParser();
            }
            case XLSX -> {
                return new XExcelParser();
            }
            case HWP -> {
                if (fileDto.getOriginFileName().endsWith(".hwp")) {
                    return new HwpParser();
                } else {
                    throw new IllegalArgumentException();
                }
            }
            case ZIP -> {
                return new ZipParser();
            }
            default -> throw new IllegalArgumentException();
        }
    }
}
