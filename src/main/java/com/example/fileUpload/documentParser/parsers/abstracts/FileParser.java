package com.example.fileUpload.documentParser.parsers.abstracts;

import com.example.fileUpload.dto.FileDto;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public abstract class FileParser {
    public abstract void parse(FileDto fileDto) throws Exception;
}
