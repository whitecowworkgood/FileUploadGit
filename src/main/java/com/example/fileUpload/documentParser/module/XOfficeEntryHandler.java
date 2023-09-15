package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.fileUpload.documentParser.module.EmbeddedFileExtractor.parseFileName;
import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;

@Slf4j
public class XOfficeEntryHandler {
    static StringBuilder stringBuilder = new StringBuilder();
    public static final Pattern DiractoryPattern = Pattern.compile("([^/]+)\\.(\\w+)$");

    /**
     * 97-03 이후 버전의 Ms Office 문서에서 포함된 파일들을 추출합니다.
     *
     * @param fileOlePath 추출된 ole 객체의 저장경로
     * @param picture OLE를 저장하는 PackagePart라는 타입의 객체
     * */
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

                }*/ else if (root.hasEntry(OleEntry.HWPINFO.getValue())) {

                        stringBuilder.append("HWP_Document").append(FileType.HWP.getValue());
                        String fileName = stringBuilder.toString();
                        stringBuilder.setLength(0);

                        String uuid = addUniqueFileNameMapping(fileName);

                        stringBuilder.append(fileOlePath).append(File.separator).append(uuid);
                        try (FileOutputStream outputStream = new FileOutputStream(stringBuilder.toString())) {
                            log.info("try안에 들어옴");
                            outputStream.write(picture.get(i).getInputStream().readAllBytes());
                        } catch (IOException e) {
                            ExceptionUtils.getStackTrace(e);
                            log.error("한컴 파일 저장 실패");
                        }
                        stringBuilder.setLength(0);
                    }else if (root.hasEntry(OleEntry.WORD.getValue())) {

                        String fileName = parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));
                        String uuid = addUniqueFileNameMapping(fileName);

                        stringBuilder.append(fileOlePath).append(File.separator).append(uuid);
                        try (FileOutputStream outputStream = new FileOutputStream(stringBuilder.toString())) {
                            outputStream.write(picture.get(i).getInputStream().readAllBytes());
                        } catch (IOException e) {
                            ExceptionUtils.getStackTrace(e);
                            log.error("파일 저장 실패");
                        }
                        stringBuilder.setLength(0);
                    }else if (root.hasEntry(OleEntry.PPT.getValue())) {

                        String fileName = parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));
                        String uuid = addUniqueFileNameMapping(fileName);

                        stringBuilder.append(fileOlePath).append(File.separator).append(uuid);
                        try (FileOutputStream outputStream = new FileOutputStream(stringBuilder.toString())) {
                            outputStream.write(picture.get(i).getInputStream().readAllBytes());
                        } catch (IOException e) {
                            ExceptionUtils.getStackTrace(e);
                            log.error("파일 저장 실패");
                        }
                        stringBuilder.setLength(0);
                    } else if (root.hasEntry(OleEntry.XLS.getValue())) {

                        String fileName = parseFileName((DocumentEntry) root.getEntry(OleEntry.COMPOBJ.getValue()));
                        String uuid = addUniqueFileNameMapping(fileName);

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
                XSSFWorkbook workbook = null;
                byte[] oleData=null;

                if(FileUtil.getFileExtension(fileName).equals(".xlsx")){
                    log.info("xlsx파일임");
                    workbook = new XSSFWorkbook(new ByteArrayInputStream(picture.get(i).getInputStream().readAllBytes()));

                    CTBookView[] cb = workbook.getCTWorkbook().getBookViews().getWorkbookViewArray();

                    //log.info(Arrays.toString(cb));

                    cb[0].setVisibility(STVisibility.VISIBLE);
                    workbook.getCTWorkbook().getBookViews().setWorkbookViewArray(cb);

                    //log.info(Arrays.toString(cb));

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    workbook.write(outputStream);

                    oleData = outputStream.toByteArray();
                }else{
                    oleData = picture.get(i).getInputStream().readAllBytes();
                }

                String uuid = UUID.randomUUID()+FileUtil.getFileExtension(fileName);

                ExternalFileMap.addFileNameMapping(fileName,uuid);

                stringBuilder.append(fileOlePath).append(File.separator).append(uuid);


                try (FileOutputStream fileOutputStream = new FileOutputStream(stringBuilder.toString())) {
                    fileOutputStream.write(oleData);
                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                }finally {
                    IOUtils.closeQuietly(workbook);
                }
                stringBuilder.setLength(0);
            }
        }
    }
}
