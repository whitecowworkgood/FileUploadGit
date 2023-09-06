package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.FileType;
import com.example.fileUpload.unit.FileUtil;
import com.example.fileUpload.unit.OleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.filesystem.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.documentParser.module.EmbeddedFileExtractor.parseFileName;

@Slf4j
public class OfficeEntryHandler {

    public static void getParser(DirectoryEntry directoryEntry, String fileOlePath) throws IOException {

        String fileName = null;
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

        }else if (directoryEntry.hasEntry(OleEntry.HWPINFO.getValue())) {
            log.info("안에 한글파일이 있잖아!!");

            POIFSFileSystem poifs = new POIFSFileSystem();
            DirectoryEntry dst = poifs.getRoot();

            fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

            if(fileName == null){
                fileName = FileUtil.getRtNum()+FileType.HWP.getValue();
            }

            // 복사본 저장
            FileOutputStream fos = new FileOutputStream(fileOlePath+ fileName);
            poifs.writeFilesystem(fos);

            fos.close();
            poifs.close();
        }else if (directoryEntry.hasEntry(OleEntry.WORD.getValue())) {
            log.info("안에 워드파일이 있잖아!!");

            POIFSFileSystem poifs = new POIFSFileSystem();
            DirectoryEntry dst = poifs.getRoot();

            fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

            if(fileName == null){
                fileName = FileUtil.getRtNum()+FileType.HWP.getValue();
            }

            // 복사본 저장
            FileOutputStream fos = new FileOutputStream(fileOlePath+ fileName);
            poifs.writeFilesystem(fos);

            fos.close();
            poifs.close();

        }else if (directoryEntry.hasEntry(OleEntry.PPT.getValue())) {
            log.info("안에 피피티파일이 있잖아!!");

            POIFSFileSystem poifs = new POIFSFileSystem();
            DirectoryEntry dst = poifs.getRoot();

            fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

            if(fileName == null){
                fileName = FileUtil.getRtNum()+FileType.HWP.getValue();
            }

            // 복사본 저장
            FileOutputStream fos = new FileOutputStream(fileOlePath+ fileName);
            poifs.writeFilesystem(fos);

            fos.close();
            poifs.close();

        }else if (directoryEntry.hasEntry(OleEntry.XLS.getValue())) {
            log.info("안에 엑셀파일이 있잖아!!");

            POIFSFileSystem poifs = new POIFSFileSystem();
            DirectoryEntry dst = poifs.getRoot();

            fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

            if(fileName == null){
                fileName = FileUtil.getRtNum()+FileType.HWP.getValue();
            }

            // 복사본 저장
            FileOutputStream fos = new FileOutputStream(fileOlePath+ fileName);
            poifs.writeFilesystem(fos);

            fos.close();
            poifs.close();

        }
    }
}
