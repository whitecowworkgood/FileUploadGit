/*
package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.util.FileType;
import com.example.fileUpload.util.OleEntry;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;

import java.io.*;
import java.nio.charset.Charset;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removeFileExtension;
import static com.example.fileUpload.util.FileUtil.removePath;

@NoArgsConstructor
public class XOfficeEntryHandler {
    static StringBuilder stringBuilder = new StringBuilder();

    public void parser(PackagePart pPart, String OriginalFileName, String OLESavePath) throws IOException, OpenXML4JException, XmlException {

        EmbeddedFileExtractor embeddedFileExtractor = new EmbeddedFileExtractor();

        //XLS파일 처리
        if (pPart.getContentType().equals("application/vnd.ms-excel")) {

            FileOutputStream outputStream = null;
            HSSFWorkbook embeddedWorkbook = null;

            try {
                embeddedWorkbook = new HSSFWorkbook(pPart.getInputStream());
                embeddedWorkbook.setHidden(false);

                String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

                stringBuilder.append(OLESavePath).append(uuid);

                outputStream = new FileOutputStream(stringBuilder.toString());
                embeddedWorkbook.write(outputStream);

            }catch (IOException e){
                ExceptionUtils.getStackTrace(e);
            }finally{
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(embeddedWorkbook);
                IOUtils.closeQuietly(outputStream);
            }
        }
        // XLSX파일 처리
        else if (pPart.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {

            FileOutputStream outputStream = null;
            OPCPackage docPackage = null;
            XSSFWorkbook embeddedWorkbook = null;

            try {
                docPackage = OPCPackage.open(pPart.getInputStream());
                embeddedWorkbook = new XSSFWorkbook(docPackage);

                CTBookView[] cb = embeddedWorkbook.getCTWorkbook().getBookViews().getWorkbookViewArray();

                cb[0].setVisibility(STVisibility.VISIBLE);
                embeddedWorkbook.getCTWorkbook().getBookViews().setWorkbookViewArray(cb);

                String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

                stringBuilder.append(OLESavePath).append(uuid);
                outputStream = new FileOutputStream(stringBuilder.toString());
                embeddedWorkbook.write(outputStream);

            }catch (IOException e){
                ExceptionUtils.getStackTrace(e);
            }finally{
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(embeddedWorkbook);
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(docPackage);
            }

        } else if (pPart.getContentType().equals("application/vnd.ms-excel.sheet.macroEnabled.12")) {
            OPCPackage docPackage = null;
            Workbook workbook = null;
            BufferedWriter csvWriter = null;

            try {
                docPackage = OPCPackage.open(pPart.getInputStream());
                workbook = new XSSFWorkbook(docPackage);

                // 시트 선택 (시트 인덱스 또는 이름 사용 가능)
                Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택 (인덱스 0부터 시작)

                // CSV 파일 경로 및 파일명 설정
                stringBuilder.append(sheet.getSheetName()).append(".csv");
                String uuid = addUniqueFileNameMapping(stringBuilder.toString());
                stringBuilder.setLength(0);

                stringBuilder.append(OLESavePath).append(uuid);

                csvWriter = new BufferedWriter(new FileWriter(stringBuilder.toString(), Charset.forName("EUC-KR")));

                // 각 행을 반복하여 CSV로 쓰기
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String cellValue = switch (cell.getCellType()) {
                            case STRING -> cell.getStringCellValue();
                            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                            case BLANK -> "";
                            default -> "";
                        };
                        // CSV 파일에 쓰기
                        csvWriter.write(cellValue);

                        // 다음 셀에 데이터가 없으면 줄바꿈
                        if (cell.getColumnIndex() < row.getLastCellNum() - 1) {
                            csvWriter.write(",");
                        } else {
                            csvWriter.newLine();
                        }
                    }
                }

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
            }finally {
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(docPackage);
                IOUtils.closeQuietly(workbook);
                IOUtils.closeQuietly(csvWriter);
            }
        }

        // DOC문서 처리
        else if (pPart.getContentType().equals("application/msword")) {

            FileOutputStream outputStream = null;
            HWPFDocument document = null;

            try {
                document = new HWPFDocument(pPart.getInputStream());
                String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

                stringBuilder.append(OLESavePath).append(uuid);

                outputStream = new FileOutputStream(stringBuilder.toString());
                document.write(outputStream);

            }catch (IOException e){
                ExceptionUtils.getStackTrace(e);
            }finally{
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(document);
                IOUtils.closeQuietly(outputStream);
            }
        }
        // DOCX문서 처리
        else if (pPart.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            FileOutputStream outputStream = null;
            OPCPackage docPackage = null;
            XWPFDocument document = null;

            try {
                docPackage = OPCPackage.open(pPart.getInputStream());
                document = new XWPFDocument(docPackage);

                String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

                stringBuilder.append(OLESavePath).append(uuid);

                outputStream = new FileOutputStream(stringBuilder.toString());
                document.write(outputStream);

            }catch (IOException e){
                ExceptionUtils.getStackTrace(e);
            }finally{
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(docPackage);
                IOUtils.closeQuietly(document);
                IOUtils.closeQuietly(outputStream);
            }
        }
        // PPT문서 처리
        else if (pPart.getContentType().equals("application/vnd.ms-powerpoint")) {
            FileOutputStream outputStream = null;
            HSLFSlideShow slideShow = null;

            try {
                slideShow = new HSLFSlideShow(pPart.getInputStream());

                String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

                stringBuilder.append(OLESavePath).append(uuid);

                outputStream = new FileOutputStream(stringBuilder.toString());
                slideShow.write(outputStream);

            }catch (IOException e){
                ExceptionUtils.getStackTrace(e);
            }finally{
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(slideShow);
                IOUtils.closeQuietly(outputStream);
            }
        }
        // PPTX문서 처리
        else if (pPart.getContentType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
            FileOutputStream outputStream = null;
            OPCPackage docPackage = null;
            XSLFSlideShow slideShow = null;

            try {
                docPackage = OPCPackage.open(pPart.getInputStream());
                slideShow = new XSLFSlideShow(docPackage);

                String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

                stringBuilder.append(OLESavePath).append(uuid);

                outputStream = new FileOutputStream(stringBuilder.toString());
                slideShow.write(outputStream);

            }catch (IOException e){
                ExceptionUtils.getStackTrace(e);
            }finally{
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(docPackage);
                IOUtils.closeQuietly(slideShow);
                IOUtils.closeQuietly(outputStream);
            }
        }
        // HWP, 기타 OLE들 처리
        else if(pPart.getContentType().equals("application/vnd.openxmlformats-officedocument.oleObject")) {

            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(pPart.getInputStream());
            FileOutputStream outputStream = null;

            DirectoryNode directoryNode = poifsFileSystem.getRoot();

            if(directoryNode.hasEntry(OleEntry.HWPINFO.getValue())){

                stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.HWP.getValue());
                String fileName = stringBuilder.toString();
                stringBuilder.setLength(0);

                String uuid = addUniqueFileNameMapping(fileName);

                stringBuilder.append(OLESavePath).append(File.separator).append(uuid);
                try {

                    outputStream = new FileOutputStream(stringBuilder.toString());
                    poifsFileSystem.writeFilesystem(outputStream);

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                }finally {
                    stringBuilder.setLength(0);
                    IOUtils.closeQuietly(outputStream);
                    IOUtils.closeQuietly(poifsFileSystem);

                }

            }else if(directoryNode.hasEntry(OleEntry.WORD.getValue())){

                stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.RTF.getValue());
                String fileName = stringBuilder.toString();
                stringBuilder.setLength(0);

                String uuid = addUniqueFileNameMapping(fileName);

                stringBuilder.append(OLESavePath).append(File.separator).append(uuid);
                try {

                    outputStream = new FileOutputStream(stringBuilder.toString());
                    poifsFileSystem.writeFilesystem(outputStream);

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                }finally {
                    stringBuilder.setLength(0);
                    IOUtils.closeQuietly(outputStream);
                    IOUtils.closeQuietly(poifsFileSystem);

                }

            }else if(directoryNode.hasEntry(Ole10Native.OLE10_NATIVE)){
                String bmpType =embeddedFileExtractor.parseFileType((DocumentEntry) poifsFileSystem.getRoot().getEntry(OleEntry.COMPOBJ.getValue()));

                if(bmpType!=null && bmpType.equals(FileType.BMP.getValue())){
                    DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(Ole10Native.OLE10_NATIVE));
                    oleStream.skipNBytes(4);

                    stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.BMP.getValue());
                    String uuid = addUniqueFileNameMapping(stringBuilder.toString());
                    stringBuilder.setLength(0);

                    stringBuilder.append(OLESavePath).append(uuid);

                    try {
                        outputStream = new FileOutputStream(stringBuilder.toString());
                        outputStream.write(oleStream.readAllBytes());

                    } catch (IOException e) {
                        ExceptionUtils.getStackTrace(e);

                    } finally {
                        stringBuilder.setLength(0);
                        IOUtils.closeQuietly(oleStream);
                        IOUtils.closeQuietly(outputStream);
                    }

                }else{
                    DocumentEntry ole10Native = (DocumentEntry) directoryNode.getEntry(Ole10Native.OLE10_NATIVE);
                    embeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), OLESavePath);
                }


            }else if (directoryNode.hasEntry(OleEntry.ODF.getValue())) {

                DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(OleEntry.ODF.getValue()));

                if(!directoryNode.hasEntry(OleEntry.COMPOBJ.getValue())){
                    stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.ODT.getValue());
                }else{
                    stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(embeddedFileExtractor.parseFileType((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue())));
                }

                String uuid = addUniqueFileNameMapping(stringBuilder.toString());
                stringBuilder.setLength(0);

                stringBuilder.append(OLESavePath).append(uuid);

                try {
                    outputStream = new FileOutputStream(stringBuilder.toString());
                    outputStream.write(oleStream.readAllBytes());

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                } finally {
                    stringBuilder.setLength(0);
                    IOUtils.closeQuietly(oleStream);
                    IOUtils.closeQuietly(outputStream);
                }
            }
        }
    }


}



*/
