package com.example.fileUpload.documentParser.ExtractEngine;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;

@Slf4j
public class DirectoryNodeParserAdapter {
    private PackagePart packagePart;

    private POIFSFileSystem poifsFileSystem;


    public DirectoryNodeParserAdapter(PackagePart packagePart) throws IOException {

        this.packagePart = packagePart;
        //this.poifsFileSystem = new POIFSFileSystem(packagePart.getInputStream());

    }

    public void getEmbeddedFile(String savePath, String uploadFileName) throws Exception {


        //TODO 추출코드 다시 구현하기 및 CSV추출 코드 구현하기

            if(isObject()){
                this.poifsFileSystem = new POIFSFileSystem(packagePart.getInputStream());
                DirectoryNodeParser directoryNodeParser = new DirectoryNodeParser(poifsFileSystem.getRoot());
                directoryNodeParser.getEmbeddedFile(savePath, uploadFileName);

            } else if (packagePart.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                System.out.println("docx");
                writeXDocument(savePath);

            } else if (packagePart.getContentType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                System.out.println("pptx");
                writeXPowerPoint(savePath);

            } else if (isXExcel()) {
                /*XSSFWorkbook xssfWorkbook = new XSSFWorkbook(packagePart);

                if(xssfWorkbook.getCTWorkbook().isSetExtLst()){
                    log.info("xexcel");
                    saveDocument(xssfWorkbook, savePath);
                    return;
                }
                log.info("csv임");
                IOUtils.closeQuietly(xssfWorkbook);*/
                System.out.println("xlsx");


            } else if (isExcel()) {
                this.poifsFileSystem = new POIFSFileSystem(packagePart.getInputStream());
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
                hssfWorkbook.setHidden(false);
                saveDocument(hssfWorkbook, savePath);
                IOUtils.closeQuietly(hssfWorkbook);

            }else{
                this.poifsFileSystem = new POIFSFileSystem(packagePart.getInputStream());
                saveDocument(savePath);
            }




    }
    protected void writeXExcel(String savePath) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        OPCPackage poixmlDocument = OPCPackage.open(this.packagePart.getInputStream());
        XSSFWorkbook embeddedWorkbook = new XSSFWorkbook(poixmlDocument);

        CTBookView[] cb = embeddedWorkbook.getCTWorkbook().getBookViews().getWorkbookViewArray();

        cb[0].setVisibility(STVisibility.VISIBLE);
        embeddedWorkbook.getCTWorkbook().getBookViews().setWorkbookViewArray(cb);

        String uuid = addUniqueFileNameMapping(StringUtils.getFilename(String.valueOf(this.packagePart.getPartName())));

        stringBuffer.append(savePath).append(uuid);
        FileOutputStream outputStream = new FileOutputStream(stringBuffer.toString());
        BufferedOutputStream bo = new BufferedOutputStream(outputStream);
        embeddedWorkbook.write(bo);
    }

    protected void writeXDocument(String savePath) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();

        OPCPackage poixmlDocument = OPCPackage.open(this.packagePart.getInputStream());
        XWPFDocument document = new XWPFDocument(poixmlDocument);

        String uuid = addUniqueFileNameMapping(StringUtils.getFilename(String.valueOf(this.packagePart.getPartName())));

        stringBuffer.append(savePath).append(uuid);

        FileOutputStream outputStream = new FileOutputStream(stringBuffer.toString());
        BufferedOutputStream bo = new BufferedOutputStream(outputStream);
        document.write(bo);
    }

    private void writeXPowerPoint(String savePath) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        OPCPackage  poixmlDocument = OPCPackage.open(this.packagePart.getInputStream());
        XSLFSlideShow slideShow = new XSLFSlideShow(poixmlDocument);

        String uuid = addUniqueFileNameMapping(StringUtils.getFilename(String.valueOf(this.packagePart.getPartName())));

        stringBuffer.append(savePath).append(uuid);

        FileOutputStream outputStream = new FileOutputStream(stringBuffer.toString());
        BufferedOutputStream bo = new BufferedOutputStream(outputStream);
        slideShow.write(bo);
    }
    private void saveDocument(XSSFWorkbook xssfWorkbook, String savePath){
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(StringUtils.getFilename(String.valueOf(packagePart.getPartName().getName())));
        String uuid = addUniqueFileNameMapping(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.length());

        stringBuffer.append(savePath).append(uuid);

        try (FileOutputStream outputStream = new FileOutputStream(stringBuffer.toString());
             BufferedOutputStream bo = new BufferedOutputStream(outputStream)) {
            // bo.write(bytes);
            xssfWorkbook.write(bo);
        }catch(IOException e){
            ExceptionUtils.getStackTrace(e);
            log.error("파일 추출에 실패하였습니다.");
        }
    }
    private void saveDocument(HSSFWorkbook hssfWorkbook, String savePath){
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(StringUtils.getFilename(String.valueOf(packagePart.getPartName().getName())));
        String uuid = addUniqueFileNameMapping(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.length());

        stringBuffer.append(savePath).append(uuid);

        try (FileOutputStream outputStream = new FileOutputStream(stringBuffer.toString());
             BufferedOutputStream bo = new BufferedOutputStream(outputStream)) {
           // bo.write(bytes);
            hssfWorkbook.write(bo);
        }catch(IOException e){
            ExceptionUtils.getStackTrace(e);
            log.error("파일 추출에 실패하였습니다.");
        }
    }
    private void saveDocument(String savePath){
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(StringUtils.getFilename(String.valueOf(packagePart.getPartName().getName())));
        String uuid = addUniqueFileNameMapping(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.length());

        stringBuffer.append(savePath).append(uuid);
        String test = stringBuffer.toString();
        try (FileOutputStream outputStream = new FileOutputStream(test);
             BufferedOutputStream bo = new BufferedOutputStream(outputStream)) {
            //packagePart.save(bo);
             bo.write(packagePart.getInputStream().readAllBytes());
        }catch(IOException /*|OpenXML4JException*/ e){
            ExceptionUtils.getStackTrace(e);
            log.error("파일 추출에 실패하였습니다.");
        }
    }

    private boolean isObject(){
        return this.packagePart.getContentType().equals("application/vnd.openxmlformats-officedocument.oleObject");
    }

    private boolean isXExcel(){
        return this.packagePart.getContentType().equals("application/vnd.ms-excel.sheet.macroEnabled.12") ||
                this.packagePart.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }
    private boolean isExcel(){
        return this.packagePart.getContentType().equals("application/vnd.ms-excel");
    }

}
