package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.model.DocumentFile;
import com.example.fileUpload.model.File.FileDto;

public interface DocumentFileInstanceFactory {
    //DocumentFile Of(FileDto fileDto);
    DocumentFile createDocumentFileInstance(FileDto fileDto);
}
