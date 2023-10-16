package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.Ole.OleDto;
import com.example.fileUpload.repository.OleDao;
import com.example.fileUpload.util.ExternalFileMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Slf4j
@Component
public class FileProcessor {

    private final FileParserFactory fileParserFactory;
    private final OleDao oleDao;

    public void createOleExtractorHandler(FileDto fileDto) {

        try {
            OleExtractor oleExtractor = this.fileParserFactory.createParser(fileDto);
            oleExtractor.extractOleFromDocumentFile(fileDto);
            processExternalFiles(fileDto);

        }catch (Exception e) {
            catchException(e);
        }
    }

    private void catchException(Exception e){
        ExceptionUtils.getStackTrace(e);
        throw new RuntimeException(e);
    }

    private void processExternalFiles(FileDto fileDto) {
        ExternalFileMap.forEach(entry -> {
            OleDto oleDto = OleDto.builder()
                    .superId(fileDto.getId())
                    .originalFileName(entry.getKey())
                    .UUIDFileName(entry.getValue())
                    .build();
            this.oleDao.insertOle(oleDto);
        });
        ExternalFileMap.resetMap();
    }
}
