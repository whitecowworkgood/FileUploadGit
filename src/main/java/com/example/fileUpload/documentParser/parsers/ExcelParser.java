package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;

import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;


import java.io.*;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;


@NoArgsConstructor
public class ExcelParser extends OleExtractor {

    private FileInputStream fs = null;
    private HSSFWorkbook hssfWorkbook=null;
    private final OfficeEntryHandler officeEntryHandler = new OfficeEntryHandler();

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException {

        try{
            //callOfficeHandler(fileDto);
            this.fs = new FileInputStream(fileDto.getFileSavePath());

            this.hssfWorkbook= new HSSFWorkbook(this.fs);

            if(!this.hssfWorkbook.getAllEmbeddedObjects().isEmpty()){
                generateFolder(fileDto.getFileOlePath());

                for (HSSFObjectData hssfObjectData : this.hssfWorkbook.getAllEmbeddedObjects())
                    this.officeEntryHandler.parser((DirectoryNode) hssfObjectData.getDirectory(), fileDto.getOriginFileName(), fileDto.getFileOlePath());
            }



        }catch (Exception e){
            catchException(e);

        }finally{
            closeResources();
        }
    }


    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(this.fs);
        IOUtils.closeQuietly(this.hssfWorkbook);
    }
}
