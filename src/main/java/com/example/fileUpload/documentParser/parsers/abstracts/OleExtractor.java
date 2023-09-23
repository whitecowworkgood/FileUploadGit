package com.example.fileUpload.documentParser.parsers.abstracts;

import com.example.fileUpload.model.FileDto;

public abstract class OleExtractor {
    public abstract void extractOleFromDocumentFile(FileDto fileDto) throws Exception;
}
