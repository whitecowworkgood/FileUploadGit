package com.example.fileUpload.documentParser.module.OleExtractor.abstracts;

import com.example.fileUpload.documentParser.module.EmbeddedFileExtractor;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removeFileExtension;

public abstract class OleExtractor {
    protected StringBuffer stringBuffer = new StringBuffer();

    protected String  originalFileName;
    protected String oleSavePath;

    protected String fileName;

    protected EmbeddedFileExtractor embeddedFileExtractor = new EmbeddedFileExtractor();

    protected void catchIOException(IOException e){
        ExceptionUtils.getStackTrace(e);
    }
    protected void catchException(Exception e){
        ExceptionUtils.getStackTrace(e);
    }
    protected abstract void closeResources();


    protected String buildPathFileName(String fileType){
        return new StringBuffer(removeFileExtension(this.originalFileName)).append("_OLE")
                .append(fileType).toString();
    }

    protected String buildOutputPath() {
        return new StringBuffer(this.oleSavePath).append(File.separator)
                .append(addUniqueFileNameMapping(this.fileName)).toString();

    }
}
