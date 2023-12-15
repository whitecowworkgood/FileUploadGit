package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.ExtractEngine.DirectoryNodeParser;
import com.example.fileUpload.documentParser.parsers.abstracts.DocumentParser;
import com.example.fileUpload.util.Enum.OleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

@Slf4j

public class WordParser extends DocumentParser {
    private final String fileSavePath;
    private final String oleSavePath;
    private final String originalFileName;
    private HWPFDocument hwpfDocument;

    public WordParser(String fileSavePath, String oleSavePath, String originalFileName) {
        this.fileSavePath = fileSavePath;
        this.oleSavePath = oleSavePath;
        this.originalFileName=originalFileName;
    }

    @Override
    public void extractEmbeddedObjects() {
        try (FileInputStream fs = new FileInputStream(this.fileSavePath);
             BufferedInputStream bi = new BufferedInputStream(fs)) {

            this.hwpfDocument = new HWPFDocument(bi);
            callDirectoryNodeParser();
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);

        } finally {
            IOUtils.closeQuietly(this.hwpfDocument);
        }
    }

    private void callDirectoryNodeParser() throws IOException {

        if(this.hwpfDocument.getDirectory().hasEntry(OleEntry.OBJECTPOOL.getValue())){
            return;
        }

        generateFolder(this.oleSavePath);

        DirectoryNode objectPools = (DirectoryNode) this.hwpfDocument.getDirectory().getEntry(OleEntry.OBJECTPOOL.getValue());

        Iterator<Entry> it = objectPools.getEntries();
        while (it.hasNext()) {

            new DirectoryNodeParser((DirectoryNode) it.next()).getEmbeddedFile(oleSavePath, originalFileName);

        }


    }

}
