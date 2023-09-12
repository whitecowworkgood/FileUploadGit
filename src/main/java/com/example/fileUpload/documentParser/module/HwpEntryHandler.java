package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.util.FileUtil;
import com.example.fileUpload.util.OleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.*;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.fileUpload.documentParser.module.EmbeddedFileExtractor.parseFileName;
import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;

@Slf4j
public class HwpEntryHandler {
    static StringBuilder stringBuilder = new StringBuilder();
    public static void parseHwp(InputStream inputStream, String fileOlePath) {
        String fileName = null;
        try {
            // 앞의 4바이트를 제외하고 남은 데이터를 POI 읽기
            inputStream.skipNBytes(4);// 앞의 4바이트를 건너뜀
            POIFSFileSystem pof = new POIFSFileSystem(inputStream);

            DirectoryEntry root = pof.getRoot();

            if(root.hasEntry(OleEntry.WORD.getValue())){
                fileName=parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));

                //Ole, OlePrev000등을 삭제하는 코드 -> 삭제가 불필요하면 제거하기
                List<Entry> entriesToDelete = new ArrayList<>();
                Iterator<Entry> entries = root.getEntries();
                while (entries.hasNext()) {
                    Entry entry = entries.next();
                    if (entry.getName().startsWith("Ole", 1)) {
                        entriesToDelete.add(entry);
                    }
                }

                for (Entry entry : entriesToDelete) {
                    root.getEntry(entry.getName()).delete();
                }

                String uuid = addUniqueFileNameMapping(fileName);

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid).append(FileUtil.getFileExtension(fileName));
                FileOutputStream fos = new FileOutputStream(stringBuilder.toString());

                stringBuilder.setLength(0);
                pof.writeFilesystem(fos);

                IOUtils.closeQuietly(fos);

            }else if(root.hasEntry(OleEntry.PPT.getValue())){

                fileName=parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));

                //Ole, OlePrev000등을 삭제하는 코드 -> 삭제가 불필요하면 제거하기
                List<Entry> entriesToDelete = new ArrayList<>();
                Iterator<Entry> entries = root.getEntries();
                while (entries.hasNext()) {
                    Entry entry = entries.next();
                    if (entry.getName().startsWith("Ole", 1)) {
                        entriesToDelete.add(entry);
                    }
                }

                for (Entry entry : entriesToDelete) {
                    root.getEntry(entry.getName()).delete();
                }

                String uuid = addUniqueFileNameMapping(fileName);

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid).append(FileUtil.getFileExtension(fileName));
                FileOutputStream fos = new FileOutputStream(stringBuilder.toString());

                stringBuilder.setLength(0);
                pof.writeFilesystem(fos);

                IOUtils.closeQuietly(fos);

            } else if (root.hasEntry(OleEntry.XLS.getValue())) {

                fileName=parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));

                //Ole, OlePrev000등을 삭제하는 코드 -> 삭제가 불필요하면 제거하기
                List<Entry> entriesToDelete = new ArrayList<>();
                Iterator<Entry> entries = root.getEntries();
                while (entries.hasNext()) {
                    Entry entry = entries.next();
                    if (entry.getName().startsWith("Ole", 1)) {
                        entriesToDelete.add(entry);
                    }
                }

                for (Entry entry : entriesToDelete) {
                    root.getEntry(entry.getName()).delete();
                }

                String uuid = addUniqueFileNameMapping(fileName);

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid).append(FileUtil.getFileExtension(fileName));
                FileOutputStream fos = new FileOutputStream(stringBuilder.toString());

                stringBuilder.setLength(0);
                pof.writeFilesystem(fos);

                IOUtils.closeQuietly(fos);

            } else if (root.hasEntry(Ole10Native.OLE10_NATIVE)) {

                DocumentEntry ole10Native = (DocumentEntry) root.getEntry(Ole10Native.OLE10_NATIVE);

                EmbeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), fileOlePath);

            }else if (root.hasEntry(OleEntry.PACKAGE.getValue())) {

                if(root.hasEntry(OleEntry.COMPOBJ.getValue())){
                    DocumentEntry packageEntry = (DocumentEntry) root.getEntry((OleEntry.PACKAGE.getValue()));
                    EmbeddedFileExtractor.parsePackageEntry(parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue())),
                            packageEntry, fileOlePath);
                }else{

                    DocumentEntry packageEntry = (DocumentEntry) root.getEntry((OleEntry.PACKAGE.getValue()));
                    EmbeddedFileExtractor.parsePackageEntry(packageEntry, fileOlePath);
                }


            }else{
                log.info("지원이 안되는 파일이 있습니다.");
            }
            IOUtils.closeQuietly(pof);

        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
