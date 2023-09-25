package com.example.fileUpload.documentParser.module.OleExtractor.abstracts;

import com.example.fileUpload.documentParser.module.EmbeddedFileExtractor;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

public abstract class OleExtractor {
    protected  StringBuffer stringBuffer = new StringBuffer();
    protected EmbeddedFileExtractor embeddedFileExtractor = new EmbeddedFileExtractor();
    protected void catchIOException(IOException e){
        ExceptionUtils.getStackTrace(e);
    }
    protected void catchException(Exception e){
        ExceptionUtils.getStackTrace(e);
    }
    protected abstract void closeResources();

}
