package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.util.FileType;
import com.example.fileUpload.util.OleEntry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import static com.example.fileUpload.documentParser.module.EmbeddedFileExtractor.parseFileName;
import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removeFileExtension;
import static com.example.fileUpload.util.FileUtil.removePath;

public class XOfficeEntryHandler {
    static StringBuilder stringBuilder = new StringBuilder();
    public static void parser(PackagePart pPart, String OriginalFileName, String OLESavePath) throws IOException, OpenXML4JException, XmlException {

        //XLS파일 처리
        if (pPart.getContentType().equals("application/vnd.ms-excel")) {

            FileOutputStream outputStream = null;
            HSSFWorkbook embeddedWorkbook = new HSSFWorkbook(pPart.getInputStream());
            embeddedWorkbook.setHidden(false);

            String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

            stringBuilder.append(OLESavePath).append(uuid);

            try {
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

            OPCPackage docPackage = OPCPackage.open(pPart.getInputStream());
            XSSFWorkbook embeddedWorkbook = new XSSFWorkbook(docPackage);

            CTBookView[] cb = embeddedWorkbook.getCTWorkbook().getBookViews().getWorkbookViewArray();

            cb[0].setVisibility(STVisibility.VISIBLE);
            embeddedWorkbook.getCTWorkbook().getBookViews().setWorkbookViewArray(cb);

            String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

            stringBuilder.append(OLESavePath).append(uuid);

            try {
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
        // DOC문서 처리
        else if (pPart.getContentType().equals("application/msword")) {

            FileOutputStream outputStream = null;
            HWPFDocument document = new HWPFDocument(pPart.getInputStream());

            String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

            stringBuilder.append(OLESavePath).append(uuid);

            try {
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

            OPCPackage docPackage = OPCPackage.open(pPart.getInputStream());
            XWPFDocument document = new XWPFDocument(docPackage);

            String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

            stringBuilder.append(OLESavePath).append(uuid);

            try {
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
        // PPT문서 처리
        else if (pPart.getContentType().equals("application/vnd.ms-powerpoint")) {
            FileOutputStream outputStream = null;
            HSLFSlideShow slideShow = new HSLFSlideShow(pPart.getInputStream());

            String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

            stringBuilder.append(OLESavePath).append(uuid);

            try {
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

            OPCPackage docPackage = OPCPackage.open(pPart.getInputStream());
            XSLFSlideShow slideShow = new XSLFSlideShow(docPackage);

            String uuid = addUniqueFileNameMapping(removePath(String.valueOf(pPart.getPartName())));

            stringBuilder.append(OLESavePath).append(uuid);

            try {
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
                    //IOUtils.closeQuietly(pPart.getInputStream());
                    IOUtils.closeQuietly(poifsFileSystem);

                }

            }else if(directoryNode.hasEntry(Ole10Native.OLE10_NATIVE)){
                String bmpType = parseFileName((DocumentEntry) poifsFileSystem.getRoot().getEntry(OleEntry.COMPOBJ.getValue()));

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
                    EmbeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), OLESavePath);
                }

            }else if (directoryNode.hasEntry("EmbeddedOdf")) {

                DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry("EmbeddedOdf"));

                if(!directoryNode.hasEntry(OleEntry.COMPOBJ.getValue())){
                    stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.ODT.getValue());
                }else{
                    stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(parseFileName((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue())));
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
