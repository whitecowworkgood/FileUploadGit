package com.example.fileUpload.documentParser.parsers.abstracts;

import com.example.fileUpload.model.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;

public abstract class OleExtractor {
    public abstract void extractOleFromDocumentFile(FileDto fileDto) throws Exception;

    protected void catchIOException(IOException e){
        ExceptionUtils.getStackTrace(e);
    }
    protected void catchXmlException(XmlException e){
        ExceptionUtils.getStackTrace(e);
        throw new RuntimeException(e);
    }
    protected void catchException(Exception e){
        ExceptionUtils.getStackTrace(e);
    }
    protected void callOfficeHandler(FileDto fileDto) throws Exception{};

    protected abstract void closeResources();
}
