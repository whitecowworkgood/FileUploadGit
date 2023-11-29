package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.util.Enum.OleEntry;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

@Slf4j
@NoArgsConstructor
public class WordParser extends OleExtractor {
    private FileInputStream fs = null;
    private BufferedInputStream bi = null;
    private HWPFDocumentCore hwpfDocument =null;
    private final OfficeEntryHandler officeEntryHandler = new OfficeEntryHandler();

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException {


        try{

            this.fs = new FileInputStream(fileDto.getFileSavePath());
            this.bi = new BufferedInputStream(this.fs);
            this.hwpfDocument = new HWPFDocument(this.bi);

            if (this.hwpfDocument.getDirectory().hasEntry(OleEntry.OBJECTPOOL.getValue())) {

                generateFolder(fileDto.getFileOlePath());

                DirectoryNode objectPools = (DirectoryNode) this.hwpfDocument.getDirectory().getEntry(OleEntry.OBJECTPOOL.getValue());

                for (Iterator<Entry> it = objectPools.getEntries(); it.hasNext(); ) {
                    this.officeEntryHandler.parser((DirectoryNode)it.next(), fileDto.getOriginFileName(), fileDto.getFileOlePath());
                }
            }

        }catch (IOException e){
            catchIOException(e);

        }finally{
            closeResources();
        }

    }

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(this.fs);
        IOUtils.closeQuietly(this.hwpfDocument);
        IOUtils.closeQuietly(this.bi);
    }
}
