package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.HwpEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.model.FileDto;
import kr.dogfoot.hwplib.object.bindata.BinData;
import kr.dogfoot.hwplib.object.bindata.EmbeddedBinaryData;
import kr.dogfoot.hwplib.reader.HWPReader;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;



@NoArgsConstructor
@Slf4j
public class HwpParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws Exception {
        //log.info("path{}", fileDto.getFileOlePath());
        FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());
        BinData hwpFile = HWPReader.fromInputStream(fs).getBinData();

        for(EmbeddedBinaryData data:hwpFile.getEmbeddedBinaryDataList()){

            if(data.getName().endsWith(".OLE")){

               HwpEntryHandler.parseHwp(new ByteArrayInputStream(data.getData()), fileDto.getFileOlePath());

            }
        }
    }
}
