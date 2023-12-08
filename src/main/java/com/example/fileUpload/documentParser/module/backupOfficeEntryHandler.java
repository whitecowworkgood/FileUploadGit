package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.util.Enum.FileType;
import com.example.fileUpload.util.Enum.OleEntry;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.ss.extractor.EmbeddedData;
import org.apache.poi.ss.extractor.EmbeddedExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removeFileExtension;

@Slf4j
@NoArgsConstructor
public class backupOfficeEntryHandler {
    public void parser(DirectoryNode directoryNode, String OriginalFileName, String OLESavePath) throws IOException {

        for (Iterator<Entry> it = directoryNode.getEntries(); it.hasNext(); ) {
            Entry entry = it.next();
            log.info(entry.getName());

            System.out.println(directoryNode.getClass());

            switch (entry.getName()) {

                case Ole10Native.OLE10_NATIVE -> {
                    log.info("ole10native");
                    break;
                }
                case "Workbook" -> {
                    log.info(OleEntry.XLS.getValue());
                    break;
                }
                case "PowerPoint Document" -> {
                    log.info(OleEntry.PPT.getValue());
                    break;
                }
                case "WordDocument" -> {
                    log.info(OleEntry.WORD.getValue());
                    break;
                }
                case "\u0005HwpSummaryInformation" -> {
                    log.info(OleEntry.HWPINFO.getValue());
                    break;
                }
                case "EmbeddedOdf" -> {
                    log.info(OleEntry.ODF.getValue());
                    break;
                }
                case "Package" -> {
                    log.info(OleEntry.PACKAGE.getValue());
                    break;
                }
                default -> {
                    log.info("pass");
                }
            }


        }
    }
}
