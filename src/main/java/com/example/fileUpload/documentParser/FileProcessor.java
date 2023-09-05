package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Slf4j
@Component
public class FileProcessor {

    private final FileParserFactory fileParserFactory;

    public void processFiles(FileDto fileDto) {
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

}
