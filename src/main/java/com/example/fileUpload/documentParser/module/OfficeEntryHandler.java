package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.FileType;
import com.example.fileUpload.unit.FileUtil;
import com.example.fileUpload.unit.OleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.*;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.documentParser.module.EmbeddedFileExtractor.parseFileName;

@Slf4j
public class OfficeEntryHandler {
    static StringBuilder stringBuilder = new StringBuilder();

    public static void getParser(DirectoryEntry directoryEntry, String fileOlePath) throws IOException {

        String fileName = null;
        if (directoryEntry.hasEntry(OleEntry.PACKAGE.getValue())) {

            DocumentEntry packageEntry = (DocumentEntry) directoryEntry.getEntry(OleEntry.PACKAGE.getValue());

            if(directoryEntry.hasEntry(OleEntry.COMPOBJ.getValue())){
                EmbeddedFileExtractor.parsePackageEntry(parseFileName((DocumentEntry) directoryEntry.getEntry(OleEntry.COMPOBJ.getValue()))
                        , packageEntry, fileOlePath);
            }else{
                //log.info("package는 있지만, CompObj는 없음");
                EmbeddedFileExtractor.parsePackageEntry(packageEntry, fileOlePath);
            }

        } else if (directoryEntry.hasEntry(Ole10Native.OLE10_NATIVE)) {
            DocumentEntry ole10Native = (DocumentEntry) directoryEntry.getEntry(Ole10Native.OLE10_NATIVE);

            EmbeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), fileOlePath);

        }else if (directoryEntry.hasEntry(OleEntry.HWPINFO.getValue())) {
            //log.info("안에 한글파일이 있잖아!!");

            try(POIFSFileSystem poifs = new POIFSFileSystem()){
                DirectoryEntry dst = poifs.getRoot();

                fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

                if(fileName == null){

                    stringBuilder.append(FileUtil.getRtNum()).append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                }

                stringBuilder.append(fileOlePath).append(fileName);
                // 복사본 저장
                try(FileOutputStream fos = new FileOutputStream(stringBuilder.toString())){
                    stringBuilder.setLength(0);
                    poifs.writeFilesystem(fos);
                }catch (IOException e){
                    ExceptionUtils.getStackTrace(e);
                }

            }catch (IOException e){
                ExceptionUtils.getStackTrace(e);
            }



        }else if (directoryEntry.hasEntry(OleEntry.WORD.getValue())) {
            //log.info("안에 워드파일이 있잖아!!");

            try(POIFSFileSystem poifs = new POIFSFileSystem()){
                DirectoryEntry dst = poifs.getRoot();

                fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

                if(fileName == null){
                    //fileName = FileUtil.getRtNum()+FileType.HWP.getValue();
                    stringBuilder.append(FileUtil.getRtNum()).append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                }

                stringBuilder.append(fileOlePath).append(fileName);
                // 복사본 저장
                try(FileOutputStream fos = new FileOutputStream(stringBuilder.toString())){
                    stringBuilder.setLength(0);
                    poifs.writeFilesystem(fos);
                }catch (IOException e){
                    ExceptionUtils.getStackTrace(e);
                }
            }catch (IOException e){
                ExceptionUtils.getStackTrace(e);
            }


        }else if (directoryEntry.hasEntry(OleEntry.PPT.getValue())) {
            //log.info("안에 피피티파일이 있잖아!!");

            try(POIFSFileSystem poifs = new POIFSFileSystem()){
                DirectoryEntry dst = poifs.getRoot();

                fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

                if(fileName == null){
                    stringBuilder.append(FileUtil.getRtNum()).append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                    //fileName = FileUtil.getRtNum()+FileType.HWP.getValue();
                }

                stringBuilder.append(fileOlePath).append(fileName);
                // 복사본 저장
                try(FileOutputStream fos = new FileOutputStream(stringBuilder.toString())){
                    stringBuilder.setLength(0);
                    poifs.writeFilesystem(fos);
                }catch (IOException e){
                    ExceptionUtils.getStackTrace(e);
                }
            }catch (IOException e){
                ExceptionUtils.getStackTrace(e);
            }


        }else if (directoryEntry.hasEntry(OleEntry.XLS.getValue())) {
            //log.info("안에 엑셀파일이 있잖아!!");

            try(POIFSFileSystem poifs = new POIFSFileSystem()){
                DirectoryEntry dst = poifs.getRoot();

                fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

                if(fileName == null){
                    stringBuilder.append(FileUtil.getRtNum()).append(FileType.HWP.getValue());
                    //fileName = FileUtil.getRtNum()+FileType.HWP.getValue();
                    fileName = stringBuilder.toString();

                    stringBuilder.setLength(0);
                }

                stringBuilder.append(fileOlePath).append(fileName);
                // 복사본 저장
                try(FileOutputStream fos = new FileOutputStream(stringBuilder.toString())){
                    stringBuilder.setLength(0);
                    poifs.writeFilesystem(fos);
                }catch (IOException e){
                    ExceptionUtils.getStackTrace(e);
                }
            }catch (IOException e){
                ExceptionUtils.getStackTrace(e);
            }
        }
    }
}
