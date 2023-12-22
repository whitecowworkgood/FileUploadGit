package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.ExtractEngine.DirectoryNodeParser;
import com.example.fileUpload.documentParser.parsers.abstracts.DocumentParser;
import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bindata.BinData;
import kr.dogfoot.hwplib.object.bindata.EmbeddedBinaryData;
import kr.dogfoot.hwplib.object.fileheader.FileHeader;
import kr.dogfoot.hwplib.reader.HWPReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;



@Slf4j
public class HwpParser extends DocumentParser {
    private final String fileSavePath;
    private final String oleSavePath;
    private final String originalFileName;
    private BufferedInputStream bufferedInputStream;

    public HwpParser(String fileSavePath, String oleSavePath, String originalFileName) {
        this.fileSavePath = fileSavePath;
        this.oleSavePath = oleSavePath;
        this.originalFileName = originalFileName;
    }

    @Override
    public void extractEmbeddedObjects() {

        //callDirectoryNodeParser();

        try (FileInputStream fs = new FileInputStream(this.fileSavePath)) {
            bufferedInputStream = new BufferedInputStream(fs);
            callDirectoryNodeParser();

        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);

        }
    }

    private void callDirectoryNodeParser() {
        try {
            FileHeader fileHeader = new FileHeader();

            log.info("print: "+HWPReader.fromInputStream(this.bufferedInputStream).);

            /*BinData binData = HWPReader.fromInputStream(this.bufferedInputStream).getBinData();
            boolean hasOLEFile = false;

            for (EmbeddedBinaryData data : binData.getEmbeddedBinaryDataList()) {
                if (isOLEBinData(data.getName())) {
                    if (!hasOLEFile) {
                        generateFolder(oleSavePath);
                        hasOLEFile = true;
                    }

                    try (InputStream inputStream = new ByteArrayInputStream(data.getData())) {
                        inputStream.readNBytes(4);
                        POIFSFileSystem poifs = new POIFSFileSystem(inputStream);
                        new DirectoryNodeParser(poifs.getRoot()).getEmbeddedFile(oleSavePath, originalFileName);
                        IOUtils.closeQuietly(poifs);
                    }
                }
            }*/
        } catch (Exception e) {
            ExceptionUtils.getStackTrace(e);
        }
    }

    private boolean isOLEBinData(String binDataName){
        return binDataName.endsWith(".OLE");
    }


}
