package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;


import java.io.*;

import static com.example.fileUpload.documentParser.module.OfficeEntryHandler.parser;

@NoArgsConstructor
public class ExcelParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws IOException {
        FileInputStream fs = null;
        HSSFWorkbook hssfWorkbook=null;

        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            hssfWorkbook= new HSSFWorkbook(fs);

            for (HSSFObjectData hssfObjectData : hssfWorkbook.getAllEmbeddedObjects()) {

                parser((DirectoryNode) hssfObjectData.getDirectory(), fileDto.getOriginFileName(), fileDto.getFileOlePath());
            }
        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        }finally{
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(hssfWorkbook);
        }
    }
}
