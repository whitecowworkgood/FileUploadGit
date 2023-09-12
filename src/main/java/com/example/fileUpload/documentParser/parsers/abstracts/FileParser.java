package com.example.fileUpload.documentParser.parsers.abstracts;

import com.example.fileUpload.model.FileDto;

public abstract class FileParser {
    public abstract void parse(FileDto fileDto) throws Exception;
}
