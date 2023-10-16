package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Slf4j
@Component
public class FileProcessor {

    private final FileParserFactory fileParserFactory;

    public void createOleExtractorHandler(FileDto fileDto) {

        try {
            OleExtractor oleExtractor = this.fileParserFactory.createParser(fileDto);
            oleExtractor.extractOleFromDocumentFile(fileDto);

        }catch (Exception e) {
            catchException(e);
        }
    }

    private void catchException(Exception e){
        ExceptionUtils.getStackTrace(e);
        throw new RuntimeException(e);
    }
}
