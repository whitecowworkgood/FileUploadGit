package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import kr.dogfoot.hwplib.object.bindata.EmbeddedBinaryData;
import kr.dogfoot.hwplib.reader.HWPReader;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;


@NoArgsConstructor
@Slf4j
public class HwpParser extends OleExtractor {
    private final OfficeEntryHandler officeEntryHandler = new OfficeEntryHandler();
    private FileInputStream fs = null;
    private InputStream inputStream = null;
    private POIFSFileSystem poifs = null;

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws Exception {

        try{
            //callOfficeHandler(fileDto);
            this.fs = new FileInputStream(fileDto.getFileSavePath());

            /*for(EmbeddedBinaryData data:HWPReader.fromInputStream(this.fs).getBinData().getEmbeddedBinaryDataList()){

                if(data.getName().endsWith(".OLE")){

                    this.inputStream = new ByteArrayInputStream(data.getData());
                    this.inputStream.skipNBytes(4);
                    this.poifs = new POIFSFileSystem(this.inputStream);
                    this.officeEntryHandler.parser(this.poifs.getRoot(), fileDto.getOriginFileName(), fileDto.getFileOlePath());
                }
            }*/

            boolean hasOLEFile = true;

            for (EmbeddedBinaryData data : HWPReader.fromInputStream(this.fs).getBinData().getEmbeddedBinaryDataList()) {
                if (data.getName().endsWith(".OLE")) {
                    hasOLEFile = true;

                    if (hasOLEFile) {
                        generateFolder(fileDto.getFileOlePath());
                        hasOLEFile = false;

                    }
                    this.inputStream = new ByteArrayInputStream(data.getData());
                    this.inputStream.skipNBytes(4);
                    this.poifs = new POIFSFileSystem(this.inputStream);
                    this.officeEntryHandler.parser(this.poifs.getRoot(), fileDto.getOriginFileName(), fileDto.getFileOlePath());


                }
            }


        }catch (IOException e){
            catchIOException(e);
        }finally {
            closeResources();
        }

    }

/*    @Override
    protected void callOfficeHandler(FileDto fileDto) throws Exception {

    }*/

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(this.fs);
        IOUtils.closeQuietly(this.inputStream);
        IOUtils.closeQuietly(this.poifs);
    }
}
