package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Component
public class FileProcessor {

    private final FileParserFactory fileParserFactory;

    public void processFiles(FileDto fileDto) {
        List<String> fileList = null;
        String mimeType = fileDto.getFileType();

        try {

            FileParser parser = fileParserFactory.createParser(mimeType);
            parser.parse(fileDto);

        } catch (IllegalArgumentException e) {
            ExceptionUtils.getStackTrace(e);
            throw new IllegalArgumentException("Unsupported MIME type: " + fileDto.getFileType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //추후 수정하기~~
    private void saveFile(String filePath, byte[] data) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(data);
        }
    }
}
