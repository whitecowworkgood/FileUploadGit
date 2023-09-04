package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.FileType;
import com.example.fileUpload.unit.OleEntry;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.fileUpload.unit.FileUtil.getRtNum;

@Slf4j
@RequiredArgsConstructor
public class HwpEntryHandler {

    public static void parseHwp(InputStream inputStream, String fileOlePath) {
        try {
            // 앞의 4바이트를 제외하고 남은 데이터를 POI 읽기
            inputStream.skip(4);// 앞의 4바이트를 건너뜀
            POIFSFileSystem pof = new POIFSFileSystem(inputStream);
            inputStream.close();

            DirectoryEntry root = pof.getRoot();

            if(root.hasEntry("WordDocument")){
                log.info("이 파일은 97-03버전의 Document");

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

                FileOutputStream fos = new FileOutputStream(fileOlePath+"\\"+getRtNum()+ FileType.DOC.getValue());
                pof.writeFilesystem(fos);

                fos.close();

            }else if(root.hasEntry("PowerPoint Document")){
                log.info("이 파일은 97-03버전의 ppt");
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


                FileOutputStream fos = new FileOutputStream(fileOlePath+"\\"+getRtNum()+ FileType.PPT.getValue());
                pof.writeFilesystem(fos);

                fos.close();

            } else if (root.hasEntry("Workbook")) {
                log.info("이 파일은 97-03버전의 xlsx");
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


                FileOutputStream fos = new FileOutputStream(fileOlePath+"\\"+getRtNum()+ FileType.XLS.getValue());
                pof.writeFilesystem(fos);

                fos.close();

            } else if (root.hasEntry(Ole10Native.OLE10_NATIVE)) {
                log.info("Ole10Native있음");
                DocumentEntry ole10Native = (DocumentEntry) root.getEntry(Ole10Native.OLE10_NATIVE);

                EmbeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), fileOlePath);

            }else if (root.hasEntry(OleEntry.PACKAGE.getValue())) {
                log.info("Package있음");
                DocumentEntry packageEntry = (DocumentEntry) root.getEntry((OleEntry.PACKAGE.getValue()));

                EmbeddedFileExtractor.parsePackageEntry((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()), packageEntry, fileOlePath);
            }else{
                log.info("지원이 안되는 파일이 있습니다.");
            }

            pof.close();
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
        }
    }
}
