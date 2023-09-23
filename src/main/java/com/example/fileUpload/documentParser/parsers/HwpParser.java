package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import kr.dogfoot.hwplib.object.bindata.BinData;
import kr.dogfoot.hwplib.object.bindata.EmbeddedBinaryData;
import kr.dogfoot.hwplib.reader.HWPReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


@AllArgsConstructor
@Slf4j
public class HwpParser extends OleExtractor {

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws Exception {

        OfficeEntryHandler officeEntryHandler = new OfficeEntryHandler();
        FileInputStream fs = null;
        InputStream inputStream = null;
        POIFSFileSystem poifs = null;

        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            BinData hwpFile = HWPReader.fromInputStream(fs).getBinData();

            for(EmbeddedBinaryData data:hwpFile.getEmbeddedBinaryDataList()){

                if(data.getName().endsWith(".OLE")){

                    inputStream = new ByteArrayInputStream(data.getData());
                    inputStream.skipNBytes(4);
                    poifs = new POIFSFileSystem(inputStream);
                    officeEntryHandler.parser(poifs.getRoot(), fileDto.getOriginFileName(), fileDto.getFileOlePath());
                }
            }
        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        }finally {
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(poifs);
        }

    }
}
