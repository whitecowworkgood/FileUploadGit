package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.OleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Ole10Native;

import java.io.IOException;

import static com.example.fileUpload.documentParser.module.EmbeddedFileExtractor.parseFileName;

@Slf4j
public class OfficeEntryHandler {

    public static void getParser(DirectoryEntry directoryEntry, String fileOlePath) throws IOException {


        if (directoryEntry.hasEntry(OleEntry.PACKAGE.getValue())) {

            DocumentEntry packageEntry = (DocumentEntry) directoryEntry.getEntry(OleEntry.PACKAGE.getValue());

            if(directoryEntry.hasEntry(OleEntry.COMPOBJ.getValue())){
                EmbeddedFileExtractor.parsePackageEntry(parseFileName((DocumentEntry) directoryEntry.getEntry(OleEntry.COMPOBJ.getValue()))
                        , packageEntry, fileOlePath);
            }else{
                log.info("package는 있지만, CompObj는 없음");
                EmbeddedFileExtractor.parsePackageEntry(packageEntry, fileOlePath);
            }




        } else if (directoryEntry.hasEntry(Ole10Native.OLE10_NATIVE)) {
            DocumentEntry ole10Native = (DocumentEntry) directoryEntry.getEntry(Ole10Native.OLE10_NATIVE);

            EmbeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), fileOlePath);

        }
    }
}
