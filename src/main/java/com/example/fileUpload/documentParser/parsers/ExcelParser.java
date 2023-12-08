package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;

import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.ss.usermodel.ObjectData;


import java.io.*;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;


@NoArgsConstructor
public class ExcelParser extends OleExtractor {

    private FileInputStream fs = null;
    private BufferedInputStream bi = null;
    private HSSFWorkbook hssfWorkbook=null;
    private final OfficeEntryHandler officeEntryHandler = new OfficeEntryHandler();

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException {

        try{

            this.fs = new FileInputStream(fileDto.getFileTempPath());
            this.bi = new BufferedInputStream(this.fs);
            this.hssfWorkbook= new HSSFWorkbook(this.bi);


            List<HSSFObjectData> excelList = this.hssfWorkbook.getAllEmbeddedObjects();


            if(!excelList.isEmpty()){
                generateFolder(fileDto.getFileOlePath());

                for (ObjectData hssfObjectData : excelList){

                    this.officeEntryHandler.parser((DirectoryNode) hssfObjectData.getDirectory(), fileDto.getOriginFileName(), fileDto.getFileOlePath());
                }

            }



        }catch (Exception e){
            ExceptionUtils.getStackTrace(e);

        }finally{
            IOUtils.closeQuietly(this.fs);
            IOUtils.closeQuietly(this.hssfWorkbook);
            IOUtils.closeQuietly(this.bi);
        }
    }


}
