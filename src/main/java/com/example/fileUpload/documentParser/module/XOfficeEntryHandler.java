package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.fileUpload.documentParser.module.EmbeddedFileExtractor.parseFileName;
import static com.example.fileUpload.unit.ExternalFileMap.addUniqueFileNameMapping;

@Slf4j
public class XOfficeEntryHandler {
    static StringBuilder stringBuilder = new StringBuilder();
    public static final Pattern DiractoryPattern = Pattern.compile("([^/]+)\\.(\\w+)$");

    public static void getParseFile(List<PackagePart> picture, String fileOlePath) throws IOException {
        for(int i=0; i<picture.size(); i++) {
            if(picture.get(i).getContentType().equals(MimeType.OLEOBJECT.getValue())){
                try(POIFSFileSystem poifs = new POIFSFileSystem(picture.get(i).getInputStream())){
                    DirectoryEntry root = poifs.getRoot();

                    if(root.hasEntry(Ole10Native.OLE10_NATIVE)){
                        DocumentEntry ole10NativeEntry = (DocumentEntry)root.getEntry(Ole10Native.OLE10_NATIVE);
                        InputStream oleInputStream = poifs.createDocumentInputStream(ole10NativeEntry.getName());
                        EmbeddedFileExtractor.parseOle10NativeEntry(oleInputStream, fileOlePath);

                        //가끔가다 한컴워드인 경우 bin파일에 package가 있음, 없을때도 있음....? 뭐지?
                    }/*else if(root.hasEntry(OleEntry.PACKAGE.getValue())){
                   // log.info("한컴 워드임");
                    DocumentEntry Package = (DocumentEntry)root.getEntry(OleEntry.PACKAGE.getValue());
                    InputStream oleInputStream = poifs.createDocumentInputStream(Package.getName());
                    //EmbeddedFileExtractor.parsePackageEntry((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()), Package, fileOlePath);

                    try (FileOutputStream outputStream = new FileOutputStream(fileOlePath +File.separator+ FileUtil.getRtNum()+ FileType.DOCX.getValue())) {
                        outputStream.write(oleInputStream.readAllBytes());
                    } catch (IOException e) {
                        ExceptionUtils.getStackTrace(e);
                        log.error("파일 저장 실패");
                    }
                //docx파일과 같이 03이후 오피스에 한컴을 넣을 경우
                }*/ else if (root.hasEntry(OleEntry.HWPINFO.getValue())) {
                        //log.info("안에 한글파일이 있잖아!!");

                        stringBuilder.append("HWP_Document").append(FileType.HWP.getValue());
                        String fileName = stringBuilder.toString();

                        String uuid = addUniqueFileNameMapping(fileName);

                        //ExternalFileMap.addFileNameMapping(fileName,uuid+FileUtil.getFileExtension(fileName));

                        stringBuilder.append(fileOlePath).append(File.separator).append(uuid);
                        try (FileOutputStream outputStream = new FileOutputStream(stringBuilder.toString())) {
                            outputStream.write(picture.get(i).getInputStream().readAllBytes());
                        } catch (IOException e) {
                            ExceptionUtils.getStackTrace(e);
                            log.error("파일 저장 실패");
                        }
                        stringBuilder.setLength(0);
                    }else if (root.hasEntry(OleEntry.WORD.getValue())) {
                        //log.info("안에 한글파일이 있잖아!!");
                        String fileName = parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));
                        String uuid = addUniqueFileNameMapping(fileName);

                        //ExternalFileMap.addFileNameMapping(fileName,uuid+FileUtil.getFileExtension(fileName));

                        stringBuilder.append(fileOlePath).append(File.separator).append(uuid);
                        try (FileOutputStream outputStream = new FileOutputStream(stringBuilder.toString())) {
                            outputStream.write(picture.get(i).getInputStream().readAllBytes());
                        } catch (IOException e) {
                            ExceptionUtils.getStackTrace(e);
                            log.error("파일 저장 실패");
                        }
                        stringBuilder.setLength(0);
                    }else if (root.hasEntry(OleEntry.PPT.getValue())) {
                        //log.info("안에 한글파일이 있잖아!!");
                        String fileName = parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));
                        String uuid = addUniqueFileNameMapping(fileName);

                        //ExternalFileMap.addFileNameMapping(fileName,uuid+FileUtil.getFileExtension(fileName));

                        stringBuilder.append(fileOlePath).append(File.separator).append(uuid);
                        try (FileOutputStream outputStream = new FileOutputStream(stringBuilder.toString())) {
                            outputStream.write(picture.get(i).getInputStream().readAllBytes());
                        } catch (IOException e) {
                            ExceptionUtils.getStackTrace(e);
                            log.error("파일 저장 실패");
                        }
                        stringBuilder.setLength(0);
                    } else if (root.hasEntry(OleEntry.XLS.getValue())) {
                        //log.info("안에 한글파일이 있잖아!!");
                        String fileName = parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));
                        String uuid = addUniqueFileNameMapping(fileName);

                        //ExternalFileMap.addFileNameMapping(fileName,uuid+FileUtil.getFileExtension(fileName));

                        stringBuilder.append(fileOlePath).append(File.separator).append(uuid);
                        try (FileOutputStream outputStream = new FileOutputStream(stringBuilder.toString())) {
                            outputStream.write(picture.get(i).getInputStream().readAllBytes());
                        } catch (IOException e) {
                            ExceptionUtils.getStackTrace(e);
                            log.error("파일 저장 실패");
                        }
                        stringBuilder.setLength(0);
                    }
                }catch(IOException e){
                    ExceptionUtils.getStackTrace(e);
                }
                continue;
            }

            Matcher matcher = DiractoryPattern.matcher(picture.get(i).getPartName().getName());

            if(matcher.find()){
                String fileName = matcher.group(0);

                String uuid = UUID.randomUUID()+FileUtil.getFileExtension(fileName);

                ExternalFileMap.addFileNameMapping(fileName,uuid);

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid);

                try (FileOutputStream fileOutputStream = new FileOutputStream(stringBuilder.toString())) {
                    fileOutputStream.write(picture.get(i).getInputStream().readAllBytes());
                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                }
                stringBuilder.setLength(0);
            }
        }
    }
}
