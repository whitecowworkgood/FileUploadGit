package com.example.fileUpload.documentParser.parsers;


import com.example.fileUpload.documentParser.ExtractEngine.DirectoryNodeParser;
import com.example.fileUpload.documentParser.parsers.abstracts.DocumentParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;


import java.io.*;
import java.util.List;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

public class ExcelParser extends DocumentParser {

    private final String fileSavePath;
    private final String oleSavePath;
    private final String originalFileName;
    private HSSFWorkbook hssfWorkbook;

    public ExcelParser(String fileSavePath, String oleSavePath, String originalFileName) {
        this.fileSavePath = fileSavePath;
        this.oleSavePath = oleSavePath;
        this.originalFileName=originalFileName;
    }

    @Override
    public void extractEmbeddedObjects() {
        try (FileInputStream fs = new FileInputStream(this.fileSavePath);
             BufferedInputStream bi = new BufferedInputStream(fs)) {

            this.hssfWorkbook = new HSSFWorkbook(bi);
            callDirectoryNodeParser();
        } catch (IOException e) {
           ExceptionUtils.getStackTrace(e);

        } finally {
            IOUtils.closeQuietly(this.hssfWorkbook);
        }
    }

    private void callDirectoryNodeParser() throws IOException {
        List<HSSFObjectData> hssfObjectDataList = this.hssfWorkbook.getAllEmbeddedObjects();

        if(hssfObjectDataList.isEmpty()){
            return;
        }

        generateFolder(this.oleSavePath);

        for (HSSFObjectData hssfObjectData : hssfObjectDataList) {
            DirectoryEntry directory = hssfObjectData.getDirectory();

            new DirectoryNodeParser((DirectoryNode) directory).getEmbeddedFile(oleSavePath, originalFileName);

        }
    }
}
