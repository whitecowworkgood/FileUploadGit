package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.ExternalFileMap;
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
import java.util.UUID;

import static com.example.fileUpload.documentParser.module.EmbeddedFileExtractor.parseFileName;
import static com.example.fileUpload.unit.ExternalFileMap.addUniqueFileNameMapping;

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
                    stringBuilder.append("HWP_Document").append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                }

                /*String randomName = UUID.randomUUID().toString();
                ExternalFileMap.addFileNameMapping(fileName, randomName+FileType.HWP.getValue());*/

                String uuid = addUniqueFileNameMapping(fileName);

                //ExternalFileMap.addFileNameMapping(fileName,uuid+FileUtil.getFileExtension(fileName));

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid);

                //stringBuilder.append(fileOlePath).append(randomName).append(FileType.HWP.getValue());


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
                    stringBuilder.append("HWP_Document").append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                }

                /*String randomName = UUID.randomUUID().toString();
                ExternalFileMap.addFileNameMapping(fileName, randomName+FileType.DOC.getValue());

                stringBuilder.append(fileOlePath).append(randomName).append(FileType.DOC.getValue());*/

                String uuid = addUniqueFileNameMapping(fileName);

                //ExternalFileMap.addFileNameMapping(fileName,uuid+FileUtil.getFileExtension(fileName));

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid);

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
                    stringBuilder.append("HWP_Document").append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                }

                /*String randomName = UUID.randomUUID().toString();
                ExternalFileMap.addFileNameMapping(fileName, randomName+FileType.PPT.getValue());

                stringBuilder.append(fileOlePath).append(randomName).append(FileType.PPT.getValue());*/

                String uuid = addUniqueFileNameMapping(fileName);

                //ExternalFileMap.addFileNameMapping(fileName,uuid+FileUtil.getFileExtension(fileName));

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid);

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
                    stringBuilder.append("HWP_Document").append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                }

                /*String randomName = UUID.randomUUID().toString();
                ExternalFileMap.addFileNameMapping(fileName, randomName+FileType.XLS.getValue());

                stringBuilder.append(fileOlePath).append(randomName).append(FileType.XLS.getValue());*/

                String uuid = addUniqueFileNameMapping(fileName);

                //ExternalFileMap.addFileNameMapping(fileName,uuid+FileUtil.getFileExtension(fileName));

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid);

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
