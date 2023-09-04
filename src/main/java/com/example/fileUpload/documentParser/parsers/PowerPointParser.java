package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hslf.usermodel.HSLFObjectData;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
public class PowerPointParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws IOException {
        FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());

        HSLFSlideShow hslfSlideShow = new HSLFSlideShow(fs);
        List<HSLFObjectData> objects = List.of(hslfSlideShow.getEmbeddedObjects());
        for (HSLFObjectData object : objects) {
            POIFSFileSystem poifs = new POIFSFileSystem(object.getInputStream());

            OfficeEntryHandler.getParser(poifs.getRoot(), fileDto.getFileOlePath());
            fs.close();
        }
        hslfSlideShow.close();
    }
}
