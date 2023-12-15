package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.ExtractEngine.DirectoryNodeParser;
import com.example.fileUpload.documentParser.parsers.abstracts.DocumentParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.usermodel.HSLFObjectData;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;


public class PowerPointParser extends DocumentParser {

    private final String fileSavePath;
    private final String oleSavePath;
    private final String originalFileName;
    private HSLFSlideShow hslfSlideShow;

    public PowerPointParser(String fileSavePath, String oleSavePath, String originalFileName) {
        this.fileSavePath = fileSavePath;
        this.oleSavePath = oleSavePath;
        this.originalFileName=originalFileName;
    }

    @Override
    public void extractEmbeddedObjects() {
        try (FileInputStream fs = new FileInputStream(this.fileSavePath);
             BufferedInputStream bi = new BufferedInputStream(fs)) {

            this.hslfSlideShow = new HSLFSlideShow(bi);
            callDirectoryNodeParser();
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);

        } finally {
            IOUtils.closeQuietly(this.hslfSlideShow);
        }
    }

    private void callDirectoryNodeParser() throws IOException {
        List<HSLFObjectData> hslfObjectDataList = Arrays.stream(this.hslfSlideShow.getEmbeddedObjects()).toList();

        if( hslfObjectDataList.isEmpty()){
            return;
        }

        generateFolder(this.oleSavePath);

        for (HSLFObjectData hslfObjectData :  hslfObjectDataList) {
            DirectoryEntry directory = hslfObjectData.getDirectory();

            new DirectoryNodeParser((DirectoryNode) directory).getEmbeddedFile(oleSavePath, originalFileName);

        }
    }


}
