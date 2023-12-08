package com.example.fileUpload.documentParser.parsers.abstracts;

import com.example.fileUpload.model.File.FileDto;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;

public abstract class OleExtractor {
    public abstract void extractOleFromDocumentFile(FileDto fileDto) throws Exception;


}
