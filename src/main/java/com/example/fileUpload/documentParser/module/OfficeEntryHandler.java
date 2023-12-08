package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.util.Enum.FileType;
import com.example.fileUpload.util.Enum.OleEntry;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.*;
import com.example.fileUpload.util.Enum.OleEntry;
import org.apache.poi.ss.extractor.EmbeddedData;
import org.apache.poi.ss.extractor.EmbeddedExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;


import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;

import static com.example.fileUpload.util.Enum.OleEntry.*;
import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removeFileExtension;


@Slf4j
@NoArgsConstructor
public class OfficeEntryHandler {

    static  StringBuffer stringBuffer = new StringBuffer();
    public void parser(DirectoryNode directoryNode, String OriginalFileName, String OLESavePath) throws IOException {

        EmbeddedFileExtractor embeddedFileExtractor = new EmbeddedFileExtractor();

        FileOutputStream outputStream = null;
        BufferedOutputStream bo = null;

        CompObj compObj1 = CompObj.createFromEmbeddedCompObj(directoryNode);
        log.info(compObj1.getFileExtraction());
        //테스트용

        if(directoryNode.hasEntry(Ole10Native.OLE10_NATIVE)){

            String bmpType = embeddedFileExtractor.parseFileType((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue()));

            if(bmpType!=null && bmpType.equals(FileType.BMP.getValue())){
                DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(Ole10Native.OLE10_NATIVE));
                oleStream.skipNBytes(4);

                stringBuffer.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.BMP.getValue());
                String uuid = addUniqueFileNameMapping(stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());

                stringBuffer.append(OLESavePath).append(uuid);

                try {
                    outputStream = new FileOutputStream(stringBuffer.toString());
                    bo = new BufferedOutputStream(outputStream);
                    bo.write(oleStream.readAllBytes());
                    //outputStream.write(oleStream.readAllBytes());

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                } finally {
                    stringBuffer.delete(0, stringBuffer.length());
                    IOUtils.closeQuietly(oleStream);
                    IOUtils.closeQuietly(outputStream);
                    IOUtils.closeQuietly(bo);
                }

            }else{

                embeddedFileExtractor.parserOleNativeEntry(directoryNode, OLESavePath);
            }


        }
        else if(directoryNode.hasEntry(OleEntry.XLS.getValue())){

            if (directoryNode.hasEntry("\u0001Ole")) {
                directoryNode.getEntry("\u0001Ole").delete();
            }

            EmbeddedExtractor extractor = new EmbeddedExtractor();
            EmbeddedData embeddedData = extractor.extractOne(directoryNode);

            HSSFWorkbook hs = new HSSFWorkbook(new ByteArrayInputStream(embeddedData.getEmbeddedData()));
            hs.setHidden(false);

            stringBuffer.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.XLS.getValue());
            String uuid = addUniqueFileNameMapping(stringBuffer.toString());
            stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append(OLESavePath).append(uuid);

            try {
                outputStream = new FileOutputStream(stringBuffer.toString());
                bo = new BufferedOutputStream(outputStream);
                hs.write(bo);
                //hs.write(outputStream);

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            } finally {
                stringBuffer.delete(0, stringBuffer.length());
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(hs);
                IOUtils.closeQuietly(bo);
            }

        }
        else if(directoryNode.hasEntry(OleEntry.PPT.getValue())){
            if (directoryNode.hasEntry("\u0001Ole")) {
                directoryNode.getEntry("\u0001Ole").delete();
            }
            EmbeddedExtractor extractor = new EmbeddedExtractor();
            EmbeddedData embeddedData = extractor.extractOne(directoryNode);

            stringBuffer.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.PPT.getValue());
            String uuid = addUniqueFileNameMapping(stringBuffer.toString());
            stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append(OLESavePath).append(uuid);

            try {
                outputStream = new FileOutputStream(stringBuffer.toString());
                bo = new BufferedOutputStream(outputStream);
                bo.write(embeddedData.getEmbeddedData());
                //outputStream.write(embeddedData.getEmbeddedData());

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            } finally {
                stringBuffer.delete(0, stringBuffer.length());
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(bo);
            }

        }
        else if(directoryNode.hasEntry(OleEntry.WORD.getValue())){
            EmbeddedExtractor extractor = new EmbeddedExtractor();


            if (directoryNode.hasEntry("\u0001Ole")) {
                directoryNode.getEntry("\u0001Ole").delete();
            }

            EmbeddedData embeddedData = extractor.extractOne(directoryNode);

            stringBuffer.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.DOC.getValue());
            String uuid = addUniqueFileNameMapping(stringBuffer.toString());
            stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append(OLESavePath).append(uuid);

            try {
                outputStream = new FileOutputStream(stringBuffer.toString());
                bo = new BufferedOutputStream(outputStream);
                bo.write(embeddedData.getEmbeddedData());
                //outputStream.write(embeddedData.getEmbeddedData());

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            } finally {
                stringBuffer.delete(0, stringBuffer.length());
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(bo);
            }
        }
        else if (directoryNode.hasEntry(OleEntry.HWPINFO.getValue())) {

            if (directoryNode.hasEntry("\u0001Ole")) {
                directoryNode.getEntry("\u0001Ole").delete();
            }
            EmbeddedExtractor extractor = new EmbeddedExtractor();

            EmbeddedData embeddedData = extractor.extractOne(directoryNode);

            stringBuffer.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.HWP.getValue());
            String uuid = addUniqueFileNameMapping(stringBuffer.toString());
            stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append(OLESavePath).append(uuid);

            try {
                outputStream = new FileOutputStream(stringBuffer.toString());
                bo = new BufferedOutputStream(outputStream);
                bo.write(embeddedData.getEmbeddedData());
                //outputStream.write(embeddedData.getEmbeddedData());

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            } finally {
                stringBuffer.delete(0, stringBuffer.length());
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(bo);
            }

        }
        else if (directoryNode.hasEntry(OleEntry.ODF.getValue())) {

            DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(OleEntry.ODF.getValue()));

            if(!directoryNode.hasEntry(OleEntry.COMPOBJ.getValue())){
                stringBuffer.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.ODT.getValue());
            }else{
                stringBuffer.append(removeFileExtension(OriginalFileName)).append("_OLE").append(embeddedFileExtractor.parseFileType((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue())));
            }

            String uuid = addUniqueFileNameMapping(stringBuffer.toString());
            stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append(OLESavePath).append(uuid);

            try {
                outputStream = new FileOutputStream(stringBuffer.toString());
                bo = new BufferedOutputStream(outputStream);
                bo.write(oleStream.readAllBytes());
                //outputStream.write(oleStream.readAllBytes());

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            } finally {
                stringBuffer.delete(0, stringBuffer.length());
                IOUtils.closeQuietly(oleStream);
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(bo);
            }

        }
        else if (directoryNode.hasEntry(OleEntry.PACKAGE.getValue())) {
            DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(OleEntry.PACKAGE.getValue()));

            String type = embeddedFileExtractor.parseFileType((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue()));

            if(type!=null && type.equals(FileType.XLSX.getValue())){
                XSSFWorkbook xs = new XSSFWorkbook(oleStream);

                CTBookView[] cb = xs.getCTWorkbook().getBookViews().getWorkbookViewArray();

                cb[0].setVisibility(STVisibility.VISIBLE);
                xs.getCTWorkbook().getBookViews().setWorkbookViewArray(cb);

                stringBuffer.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.XLSX.getValue());
                String uuid = addUniqueFileNameMapping(stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());

                stringBuffer.append(OLESavePath).append(uuid);

                try {
                    outputStream = new FileOutputStream(stringBuffer.toString());
                    bo = new BufferedOutputStream(outputStream);
                    xs.write(bo);
                    //xs.write(outputStream);

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                } finally {
                    stringBuffer.delete(0, stringBuffer.length());
                    IOUtils.closeQuietly(oleStream);
                    IOUtils.closeQuietly(outputStream);
                    IOUtils.closeQuietly(xs);
                    IOUtils.closeQuietly(bo);
                }

            }else if(type!=null && type.equals(FileType.CSV.getValue())){

                Workbook workbook = null;
                BufferedWriter csvWriter = null;

                try {
                    oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(OleEntry.PACKAGE.getValue()));
                    workbook = new XSSFWorkbook(oleStream);

                    // 시트 선택 (시트 인덱스 또는 이름 사용 가능)
                    Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택 (인덱스 0부터 시작)

                    // CSV 파일 경로 및 파일명 설정
                    stringBuffer.append(sheet.getSheetName()).append(".csv");
                    String uuid = addUniqueFileNameMapping(stringBuffer.toString());
                    stringBuffer.delete(0, stringBuffer.length());

                    stringBuffer.append(OLESavePath).append(uuid);

                    csvWriter = new BufferedWriter(new FileWriter(stringBuffer.toString(), Charset.forName("EUC-KR")));

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
                    stringBuffer.delete(0, stringBuffer.length());
                    IOUtils.closeQuietly(oleStream);
                    IOUtils.closeQuietly(workbook);
                    IOUtils.closeQuietly(csvWriter);
                }

            } else{
                stringBuffer.append(removeFileExtension(OriginalFileName)).append("_OLE").append(type);

                String uuid = addUniqueFileNameMapping(stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());

                stringBuffer.append(OLESavePath).append(uuid);

                try {
                    outputStream = new FileOutputStream(stringBuffer.toString());
                    bo = new BufferedOutputStream(outputStream);
                    bo.write(oleStream.readAllBytes());
                    //outputStream.write(oleStream.readAllBytes());

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                } finally {
                    stringBuffer.delete(0, stringBuffer.length());
                    IOUtils.closeQuietly(oleStream);
                    IOUtils.closeQuietly(outputStream);
                    IOUtils.closeQuietly(bo);
                }
            }

        }

    }

}
