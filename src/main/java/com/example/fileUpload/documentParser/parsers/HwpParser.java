package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.HwpEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.model.FileDto;
import kr.dogfoot.hwplib.object.bindata.BinData;
import kr.dogfoot.hwplib.object.bindata.EmbeddedBinaryData;
import kr.dogfoot.hwplib.reader.HWPReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;


@AllArgsConstructor
@Slf4j
public class HwpParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws Exception {

        HwpEntryHandler hwpEntryHandler = new HwpEntryHandler();
        FileInputStream fs = null;

        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            BinData hwpFile = HWPReader.fromInputStream(fs).getBinData();

            for(EmbeddedBinaryData data:hwpFile.getEmbeddedBinaryDataList()){

                if(data.getName().endsWith(".OLE")){
                    hwpEntryHandler.parser(new ByteArrayInputStream(data.getData()), fileDto);
                }
            }
        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        }finally {
            IOUtils.closeQuietly(fs);
        }

    }
}
