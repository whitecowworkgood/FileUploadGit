package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.util.OleEntry;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
@NoArgsConstructor
public class WordParser extends OleExtractor {


    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException, InvalidFormatException {

        HWPFDocumentCore hwpfDocument =null;
        OfficeEntryHandler officeEntryHandler = new OfficeEntryHandler();

        try{
            hwpfDocument = new HWPFDocument(new FileInputStream(fileDto.getFileSavePath()));

            if (hwpfDocument.getDirectory().hasEntry(OleEntry.OBJECTPOOL.getValue())) {
                DirectoryNode objectPools = (DirectoryNode) hwpfDocument.getDirectory().getEntry(OleEntry.OBJECTPOOL.getValue());

                for (Iterator<Entry> it = objectPools.getEntries(); it.hasNext(); ) {

                    officeEntryHandler.parser((DirectoryNode)it.next(), fileDto.getOriginFileName(), fileDto.getFileOlePath());
                }
            }

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        }finally{
            IOUtils.closeQuietly(hwpfDocument);
        }

    }

}
