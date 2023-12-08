package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.usermodel.HSLFObjectData;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.ObjectData;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;


@NoArgsConstructor
public class PowerPointParser extends OleExtractor {
    private FileInputStream fs =null;
    private BufferedInputStream bi = null;
    private POIFSFileSystem poifs =null;
    private HSLFSlideShow hslfSlideShow =null;
    private final OfficeEntryHandler officeEntryHandler = new OfficeEntryHandler();

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException {

        try{

            this.fs = new FileInputStream(fileDto.getFileTempPath());
            this.bi = new BufferedInputStream(this.fs);
            this.hslfSlideShow = new HSLFSlideShow(this.bi);


            List<HSLFObjectData> pptList = Arrays.stream(this.hslfSlideShow.getEmbeddedObjects()).toList();


            if(!pptList.isEmpty()){
                generateFolder(fileDto.getFileOlePath());

                for (HSLFObjectData object : pptList) {

                    System.out.println(object.getDirectory().getEntryNames());
                    this.officeEntryHandler.parser((DirectoryNode) object.getDirectory(), fileDto.getOriginFileName(), fileDto.getFileOlePath());

                    /*IOUtils.closeQuietly(this.poifs);
                    this.poifs = new POIFSFileSystem(object.getInputStream());
                    this.officeEntryHandler.parser(this.poifs.getRoot(), fileDto.getOriginFileName(), fileDto.getFileOlePath());*/

                }
            }



        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);

        }finally {
            IOUtils.closeQuietly(this.fs);
            IOUtils.closeQuietly(this.poifs);
            IOUtils.closeQuietly(this.hslfSlideShow);
            IOUtils.closeQuietly(this.bi);
        }

    }

}
