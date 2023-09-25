package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;


import java.io.*;


@NoArgsConstructor
public class ExcelParser extends OleExtractor {

    FileInputStream fs = null;
    HSSFWorkbook hssfWorkbook=null;
    OfficeEntryHandler officeEntryHandler = new OfficeEntryHandler();

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException {

        try{
            callOfficeHandler(fileDto);

        }catch (IOException e){
            catchIOException(e);

        }finally{
            closeResources();
        }
    }

    @Override
    protected void callOfficeHandler(FileDto fileDto) throws IOException {
        fs = new FileInputStream(fileDto.getFileSavePath());
        hssfWorkbook= new HSSFWorkbook(fs);

        for (HSSFObjectData hssfObjectData : hssfWorkbook.getAllEmbeddedObjects())
            officeEntryHandler.parser((DirectoryNode) hssfObjectData.getDirectory(), fileDto.getOriginFileName(), fileDto.getFileOlePath());
    }

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(fs);
        IOUtils.closeQuietly(hssfWorkbook);
    }
}
