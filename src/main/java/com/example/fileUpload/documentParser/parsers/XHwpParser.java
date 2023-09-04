package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

//
public class XHwpParser extends FileParser {
    @Override
    public void parse(FileDto fileDto) throws FileNotFoundException {
        FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());
    }
}
