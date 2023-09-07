package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;

@Slf4j
public class XHwpParser extends FileParser {
    @Override
    public void parse(FileDto fileDto) throws Exception {
        FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());
        log.info("여기까진 들어옴");
        fs.close();
    }
}
