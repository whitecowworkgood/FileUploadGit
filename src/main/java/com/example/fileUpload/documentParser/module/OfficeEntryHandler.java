package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.util.FileType;
import com.example.fileUpload.util.OleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.documentParser.module.EmbeddedFileExtractor.parseFileName;
import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;

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
                EmbeddedFileExtractor.parsePackageEntry(packageEntry, fileOlePath);
            }

        } else if (directoryEntry.hasEntry(Ole10Native.OLE10_NATIVE)) {
            DocumentEntry ole10Native = (DocumentEntry) directoryEntry.getEntry(Ole10Native.OLE10_NATIVE);

            EmbeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), fileOlePath);

        }else if (directoryEntry.hasEntry(OleEntry.HWPINFO.getValue())) {

            try(POIFSFileSystem poifs = new POIFSFileSystem()){
                DirectoryEntry dst = poifs.getRoot();

                fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

                if(fileName == null){
                    stringBuilder.append("HWP_Document").append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                }

                String uuid = addUniqueFileNameMapping(fileName);

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid);

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

            try(POIFSFileSystem poifs = new POIFSFileSystem()){
                DirectoryEntry dst = poifs.getRoot();

                fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

                if(fileName == null){
                    stringBuilder.append("HWP_Document").append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                }

                String uuid = addUniqueFileNameMapping(fileName);

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid);

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

            try(POIFSFileSystem poifs = new POIFSFileSystem()){
                DirectoryEntry dst = poifs.getRoot();
                fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

                if(fileName == null){
                    stringBuilder.append("HWP_Document").append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                }

                String uuid = addUniqueFileNameMapping(fileName);

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid);

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

            try(POIFSFileSystem poifs = new POIFSFileSystem()){
                DirectoryEntry dst = poifs.getRoot();

                fileName = EmbeddedFileExtractor.copyDirectory(directoryEntry, dst, directoryEntry.getName());

                if(fileName == null){
                    stringBuilder.append("HWP_Document").append(FileType.HWP.getValue());
                    fileName = stringBuilder.toString();
                    stringBuilder.setLength(0);
                }

                String uuid = addUniqueFileNameMapping(fileName);

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid);

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
