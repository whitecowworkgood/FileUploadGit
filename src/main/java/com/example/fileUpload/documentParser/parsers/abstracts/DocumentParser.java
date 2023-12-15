package com.example.fileUpload.documentParser.parsers.abstracts;

import com.example.fileUpload.model.File.FileDto;

public abstract class DocumentParser {
    //public abstract void extractOleFromDocumentFile(FileDto fileDto) throws Exception;

    public abstract void extractEmbeddedObjects();


}
