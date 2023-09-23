package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
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

            OleExtractor oleExtractor = fileParserFactory.createParser(mimeType, fileDto.getOriginFileName());
            oleExtractor.extractOleFromDocumentFile(fileDto);

        } catch (IllegalArgumentException e) {
            ExceptionUtils.getStackTrace(e);
            throw new IllegalArgumentException("Unsupported MIME type: " + fileDto.getFileType());
        } catch (Exception e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException(e);
        }
    }
}
