package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;

import java.io.IOException;

public class OtherParser extends OleExtractor {
    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws Exception {

    }

    @Override
    protected void catchIOException(IOException e) {
        super.catchIOException(e);
    }

    @Override
    protected void catchException(Exception e) {
        super.catchException(e);
    }

    @Override
    protected void closeResources() {

    }
}
