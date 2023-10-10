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
            createOleExtractor(fileDto);

        } catch (IllegalArgumentException i) {
            catchIllegal(i);

        } catch (Exception e) {
            catchException(e);
        }
    }
    private void createOleExtractor(FileDto fileDto) throws Exception {
        OleExtractor oleExtractor = fileParserFactory.createParser(fileDto);
        oleExtractor.extractOleFromDocumentFile(fileDto);
    }
    private void catchIllegal(IllegalArgumentException i){
        ExceptionUtils.getStackTrace(i);
        throw new IllegalArgumentException("Unsupported MIME type");
    }
    private void catchException(Exception e){
        ExceptionUtils.getStackTrace(e);
        throw new RuntimeException(e);
    }
}
