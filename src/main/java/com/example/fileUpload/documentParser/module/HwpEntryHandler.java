package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.IOException;
import java.io.InputStream;

@NoArgsConstructor
public class HwpEntryHandler {

    public void parser(InputStream inputStream, FileDto fileDto){
        POIFSFileSystem pof =null;
        OfficeEntryHandler officeEntryHandler = new OfficeEntryHandler();
        try{
            inputStream.skipNBytes(4);
            pof = new POIFSFileSystem(inputStream);
            officeEntryHandler.parser(pof.getRoot(), fileDto.getOriginFileName(), fileDto.getFileOlePath());

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        }finally {
            IOUtils.closeQuietly(pof);
            IOUtils.closeQuietly(inputStream);
        }
    }
}
