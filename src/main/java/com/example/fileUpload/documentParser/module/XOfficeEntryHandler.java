package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.FileType;
import com.example.fileUpload.unit.FileUtil;
import com.example.fileUpload.unit.MimeType;
import com.example.fileUpload.unit.OleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.fileUpload.documentParser.module.EmbeddedFileExtractor.parseFileName;

@Slf4j
public class XOfficeEntryHandler {

    public static final Pattern DiractoryPattern = Pattern.compile("([^/]+)\\.(\\w+)$");

    public static void getParseFile(List<PackagePart> picture, String fileOlePath) throws IOException {
        for(int i=0; i<picture.size(); i++) {
            if(picture.get(i).getContentType().equals(MimeType.OLEOBJECT.getValue())){

                POIFSFileSystem poifs = new POIFSFileSystem(picture.get(i).getInputStream());
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

                    try (FileOutputStream outputStream = new FileOutputStream(fileOlePath +"\\"+ FileUtil.getRtNum()+ FileType.DOCX.getValue())) {
                        outputStream.write(oleInputStream.readAllBytes());
                    } catch (IOException e) {
                        ExceptionUtils.getStackTrace(e);
                        log.error("파일 저장 실패");
                    }
                //docx파일과 같이 03이후 오피스에 한컴을 넣을 경우
                }*/ else if (root.hasEntry(OleEntry.HWPINFO.getValue())) {
                    //log.info("안에 한글파일이 있잖아!!");

                    try (FileOutputStream outputStream = new FileOutputStream(fileOlePath +"\\"+ FileUtil.getRtNum()+FileType.HWP.getValue())) {
                        outputStream.write(picture.get(i).getInputStream().readAllBytes());
                    } catch (IOException e) {
                        ExceptionUtils.getStackTrace(e);
                        log.error("파일 저장 실패");
                    }
                }else if (root.hasEntry("WordDocument")) {
                    //log.info("안에 한글파일이 있잖아!!");
                    String fileName = parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));
                    try (FileOutputStream outputStream = new FileOutputStream(fileOlePath +"\\"+ fileName)) {
                        outputStream.write(picture.get(i).getInputStream().readAllBytes());
                    } catch (IOException e) {
                        ExceptionUtils.getStackTrace(e);
                        log.error("파일 저장 실패");
                    }
                }else if (root.hasEntry("PowerPoint Document")) {
                    //log.info("안에 한글파일이 있잖아!!");
                    String fileName = parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));

                    try (FileOutputStream outputStream = new FileOutputStream(fileOlePath +"\\"+ fileName)) {
                        outputStream.write(picture.get(i).getInputStream().readAllBytes());
                    } catch (IOException e) {
                        ExceptionUtils.getStackTrace(e);
                        log.error("파일 저장 실패");
                    }
                } else if (root.hasEntry("Workbook")) {
                    //log.info("안에 한글파일이 있잖아!!");
                    String fileName = parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));

                    try (FileOutputStream outputStream = new FileOutputStream(fileOlePath +"\\"+fileName)) {
                        outputStream.write(picture.get(i).getInputStream().readAllBytes());
                    } catch (IOException e) {
                        ExceptionUtils.getStackTrace(e);
                        log.error("파일 저장 실패");
                    }
                }
                poifs.close();
                continue;
            }

            Matcher matcher = DiractoryPattern.matcher(picture.get(i).getPartName().getName());

            if(matcher.find()){
                String fileNameAndExtension = matcher.group(0);


                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOlePath +"\\"+ fileNameAndExtension)) {
                    fileOutputStream.write(picture.get(i).getInputStream().readAllBytes());
                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                }

            }
        }
    }
}
