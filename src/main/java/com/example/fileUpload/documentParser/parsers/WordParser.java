package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
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

import static com.example.fileUpload.documentParser.module.OfficeEntryHandler.parser;

@Slf4j
@NoArgsConstructor
public class WordParser extends FileParser {


    @Override
    public void parse(FileDto fileDto) throws IOException, InvalidFormatException {
        //log.info("파서 돌입!");

        HWPFDocumentCore hwpfDocument =null;

        try{
            hwpfDocument = new HWPFDocument(new FileInputStream(fileDto.getFileSavePath()));

            if (hwpfDocument.getDirectory().hasEntry(OleEntry.OBJECTPOOL.getValue())) {
                DirectoryNode objectPools = (DirectoryNode) hwpfDocument.getDirectory().getEntry(OleEntry.OBJECTPOOL.getValue());

                for (Iterator<Entry> it = objectPools.getEntries(); it.hasNext(); ) {

                    Entry entry = it.next();

                    parser((DirectoryNode)entry, fileDto.getOriginFileName(), fileDto.getFileOlePath());
                }
            }

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        }finally{
            IOUtils.closeQuietly(hwpfDocument);
        }

    }

}
