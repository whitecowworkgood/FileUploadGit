package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.usermodel.HSLFObjectData;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static com.example.fileUpload.documentParser.module.OfficeEntryHandler.parser;


@NoArgsConstructor
public class PowerPointParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws IOException {
        FileInputStream fs =null;
        POIFSFileSystem poifs =null;
        HSLFSlideShow hslfSlideShow =null;
        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            hslfSlideShow = new HSLFSlideShow(fs);

            List<HSLFObjectData> objects = List.of(hslfSlideShow.getEmbeddedObjects());
            for (HSLFObjectData object : objects) {

                IOUtils.closeQuietly(poifs);

                poifs = new POIFSFileSystem(object.getInputStream());

                parser(poifs.getRoot(), fileDto.getOriginFileName(), fileDto.getFileOlePath());

            }
        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        }finally {
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(poifs);
            IOUtils.closeQuietly(hslfSlideShow);
        }

    }
}
