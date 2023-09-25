package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
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


@NoArgsConstructor
public class PowerPointParser extends OleExtractor {
    FileInputStream fs =null;
    POIFSFileSystem poifs =null;
    HSLFSlideShow hslfSlideShow =null;
    OfficeEntryHandler officeEntryHandler = new OfficeEntryHandler();

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException {

        try{
            callOfficeHandler(fileDto);

        }catch (IOException e){
            catchIOException(e);

        }finally {
            closeResources();
        }

    }

    @Override
    protected void callOfficeHandler(FileDto fileDto) throws IOException {
        fs = new FileInputStream(fileDto.getFileSavePath());
        hslfSlideShow = new HSLFSlideShow(fs);

        List<HSLFObjectData> objects = List.of(hslfSlideShow.getEmbeddedObjects());
        for (HSLFObjectData object : objects) {

            IOUtils.closeQuietly(poifs);

            poifs = new POIFSFileSystem(object.getInputStream());

            officeEntryHandler.parser(poifs.getRoot(), fileDto.getOriginFileName(), fileDto.getFileOlePath());

        }
    }

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(fs);
        IOUtils.closeQuietly(poifs);
        IOUtils.closeQuietly(hslfSlideShow);
    }
}
