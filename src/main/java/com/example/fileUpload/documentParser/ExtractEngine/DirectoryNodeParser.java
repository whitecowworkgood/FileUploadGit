package com.example.fileUpload.documentParser.ExtractEngine;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hpsf.ClassIDPredefined;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.ss.extractor.EmbeddedData;
import org.apache.poi.ss.extractor.EmbeddedExtractor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;

import java.io.*;
import java.nio.charset.Charset;
import java.util.StringJoiner;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removeFileExtension;

@Slf4j
public class DirectoryNodeParser {

    private DirectoryNode directoryNode;
    private String fileExtension;
    private EmbeddedExtractor embeddedExtractor = new EmbeddedExtractor();

    public DirectoryNodeParser(DirectoryNode directoryNode) {

        this.directoryNode = directoryNode;

        MakeClassIDPredefined makeClassIDPredefined = MakeClassIDPredefined.lookup(directoryNode.getStorageClsid());

        if(makeClassIDPredefined != null){
            this.fileExtension= makeClassIDPredefined.getFileExtension();

        }else{
            ClassIDPredefined classIDPredefined = ClassIDPredefined.lookup(directoryNode.getStorageClsid());
            this.fileExtension= classIDPredefined.getFileExtension();

        }
    }

    public void getEmbeddedFile(String savePath, String uploadFileName) throws IOException{

        if(isBinFile()){
            EmbeddedData embeddedData = embeddedExtractor.extractOne(directoryNode);
            saveEmbeddedData(embeddedData.getEmbeddedData(), savePath, embeddedData.getFilename());

        } else if (fileExtension.equals(".bmp")) {
            DocumentInputStream dis = directoryNode.createDocumentInputStream(Ole10Native.OLE10_NATIVE);
            dis.skipNBytes(4);
            saveEmbeddedDocument(dis.readAllBytes(), savePath, uploadFileName);
            IOUtils.closeQuietly(dis);
            
        } else if (isModernFile()) {

            DocumentInputStream dis = directoryNode.createDocumentInputStream("Package");
            saveEmbeddedDocument(dis.readAllBytes(), savePath, uploadFileName);
            IOUtils.closeQuietly(dis);

        } else if (isODFFile()) {
            DocumentInputStream dis = directoryNode.createDocumentInputStream("EmbeddedOdf");
            saveEmbeddedDocument(dis.readAllBytes(), savePath, uploadFileName);
            IOUtils.closeQuietly(dis);

        } else if(fileExtension.equals(".xlsm")){
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(directoryNode.createDocumentInputStream("Package"));

            if(xssfWorkbook.getCTWorkbook().isSetExtLst()){
                saveEmbeddedData(xssfWorkbook, savePath, uploadFileName);
                return;
            }

            Sheet sheet = xssfWorkbook.getSheetAt(0);
            saveEmbeddedData(sheet, savePath, ".csv");

            IOUtils.closeQuietly(xssfWorkbook);

        }else{
            removeOleEntry();
            EmbeddedData embeddedData = embeddedExtractor.extractOne(directoryNode);
            saveEmbeddedDocument(embeddedData.getEmbeddedData(), savePath, uploadFileName);
        }

    }
    private void saveEmbeddedData(XSSFWorkbook xssfWorkbook, String savePath, String FileName) throws IOException {

        StringBuffer stringBuffer = new StringBuffer();

        CTBookView[] cb = xssfWorkbook.getCTWorkbook().getBookViews().getWorkbookViewArray();
        cb[0].setVisibility(STVisibility.VISIBLE);
        xssfWorkbook.getCTWorkbook().getBookViews().setWorkbookViewArray(cb);

        stringBuffer.append(removeFileExtension(FileName)).append("_OLE").append(fileExtension);
        String uuid = addUniqueFileNameMapping(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.length());

        stringBuffer.append(savePath).append(uuid);

        try(FileOutputStream fileOutputStream = new FileOutputStream(stringBuffer.toString())){
            xssfWorkbook.write(fileOutputStream);
        }

    }

    private void saveEmbeddedData(byte[] data, String savePath, String FileName) {

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(FileName);
        String uuid = addUniqueFileNameMapping(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.length());

        stringBuffer.append(savePath).append(uuid);

        try (FileOutputStream outputStream = new FileOutputStream(stringBuffer.toString());
             BufferedOutputStream bo = new BufferedOutputStream(outputStream)) {
             bo.write(data);
        }catch(IOException e){
            ExceptionUtils.getStackTrace(e);
            log.error("파일 추출에 실패하였습니다.");
        }
    }

    private void saveEmbeddedDocument(byte[] data, String savePath, String originalFileName) {

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(removeFileExtension(originalFileName)).append("_OLE").append(fileExtension);
        String uuid = addUniqueFileNameMapping(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.length());

        stringBuffer.append(savePath).append(uuid);

        try (FileOutputStream outputStream = new FileOutputStream(stringBuffer.toString());
             BufferedOutputStream bo = new BufferedOutputStream(outputStream)) {
            bo.write(data);
        }catch(IOException e){
            ExceptionUtils.getStackTrace(e);
            log.error("파일 추출에 실패하였습니다.");
        }
    }

    private void saveEmbeddedData(Sheet sheet, String savePath, String fileType) throws IOException{
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(sheet.getSheetName()).append(fileType);
        String uuid = addUniqueFileNameMapping(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.length());

        stringBuffer.append(savePath).append(uuid);

        try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter(stringBuffer.toString(), Charset.forName("EUC-KR")))){

            // 각 행을 반복하여 CSV로 쓰기
            for (Row row : sheet) {
                StringJoiner rowValues = new StringJoiner(",");
                row.forEach(cell -> {
                    switch (cell.getCellType()) {
                        case STRING -> rowValues.add(cell.getStringCellValue());
                        case NUMERIC -> rowValues.add(String.valueOf(cell.getNumericCellValue()));
                        case BOOLEAN -> rowValues.add(String.valueOf(cell.getBooleanCellValue()));
                        case BLANK -> rowValues.add("");
                        default -> rowValues.add("");
                    }
                });
                csvWriter.write(rowValues.toString());
                csvWriter.newLine();
            }

        }finally {
            stringBuffer.delete(0, stringBuffer.length());
        }
    }
    private void removeOleEntry() throws FileNotFoundException {
        if (directoryNode.hasEntry("\u0001Ole")) {
            directoryNode.getEntry("\u0001Ole").delete();
        }
    }

    private boolean isBinFile(){
        return fileExtension.equals(".bin");
    }
    private boolean isODFFile(){
        return fileExtension.equals(".odt") || fileExtension.equals(".ods") || fileExtension.equals(".odp");
    }

    private boolean isModernFile(){
        return fileExtension.equals(".docx") || fileExtension.equals(".pptx") || fileExtension.equals(".xlsx");
    }
}
