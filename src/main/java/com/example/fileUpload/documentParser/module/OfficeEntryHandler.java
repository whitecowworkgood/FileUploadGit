package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.OleEntry;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OfficeEntryHandler {

    public static void getParser(DirectoryEntry directoryEntry, String fileOlePath) throws IOException {


        if (directoryEntry.hasEntry(OleEntry.PACKAGE.getValue())) {
            DocumentEntry packageEntry = (DocumentEntry) directoryEntry.getEntry(OleEntry.PACKAGE.getValue());

            EmbeddedFileExtractor.parsePackageEntry((DocumentEntry) directoryEntry.getEntry(OleEntry.COMPOBJ.getValue()), packageEntry, fileOlePath);

        } else if (directoryEntry.hasEntry(Ole10Native.OLE10_NATIVE)) {
            DocumentEntry ole10Native = (DocumentEntry) directoryEntry.getEntry(Ole10Native.OLE10_NATIVE);

            EmbeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), fileOlePath);

        }
    }
}
