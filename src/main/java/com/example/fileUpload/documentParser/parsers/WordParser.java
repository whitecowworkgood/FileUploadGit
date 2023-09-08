package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.dto.FileDto;
import com.example.fileUpload.unit.OleEntry;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.Entry;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Slf4j
@NoArgsConstructor
public class WordParser extends FileParser {


    @Override
    public void parse(FileDto fileDto) throws IOException {
        //log.info("파서 돌입!");

       //FileInputStream fs = new FileInputStream(fileDto.getFileSavePath());
        HWPFDocumentCore hwpfDocument = new HWPFDocument(new FileInputStream(fileDto.getFileSavePath()));

        hwpfDocument.getDirectory().hasEntry(OleEntry.OBJECTPOOL.getValue());

        if (hwpfDocument.getDirectory().hasEntry(OleEntry.OBJECTPOOL.getValue())) {
            DirectoryEntry objectPool = (DirectoryEntry) hwpfDocument.getDirectory().getEntry(OleEntry.OBJECTPOOL.getValue());

            for (Iterator<Entry> it = objectPool.getEntries(); it.hasNext(); ) {
                Entry entry = it.next();
                
                OfficeEntryHandler.getParser((DirectoryEntry) objectPool.getEntry(entry.getName()), fileDto.getFileOlePath());
            }
        }

        hwpfDocument.close();
    }


}
