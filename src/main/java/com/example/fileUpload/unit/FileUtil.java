package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.usermodel.HSLFObjectData;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtil {
    public static String fileTypeString = null;
    public static final Pattern filePattern = Pattern.compile("_\\d{10}");
    public static final Pattern DiractoryPattern = Pattern.compile("([^/]+)\\.(\\w+)$");
    public static final Pattern EmbeddedFileName = Pattern.compile("^[a-zA-Z]:\\.*$");

    private static final byte[] fileNameStartPattern = new byte[] { (byte) 0x3A, 0x5C };
    private static final byte[] fileNameEndPattern = new byte[] { (byte) 0x00, 0x00, 0x00, 0x03, 0x00 };

    private static final byte[] pngStartPattern = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
    private static final byte[] pngEndPattern = new byte[] { (byte) 0x49, 0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82};
    private static final byte[] jpg1StartPattern = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
    private static final byte[] jpg2StartPattern = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1 };
    private static final byte[] jpgEndPattern = new byte[] { (byte) 0xff, (byte)0xD9 };
    private static final byte[] pdfStartPattern = new byte[]{ (byte) 0x25, 0x50, 0x44, 0x46};
    private static final byte[] pdfEndPattern = new byte[]{ (byte) 0x25, 0x25, 0x45, 0x4F, 0x46};

    public static boolean valuedDocFile(FileDto fileDto){

        List<String> validTypeList = Arrays.stream(MimeType.values())
                .map(MimeType::getValue)
                .toList();

        return validTypeList.contains(fileDto.getFileType());
    }

    public static boolean isValidPath(String defaultPath, String savePath){
        if (defaultPath == null || savePath == null || defaultPath.isEmpty() || savePath.isEmpty()) {
            return false;
        }

        if (!savePath.startsWith(defaultPath)) {
            return false;
        }

        File saveFile = new File(savePath);
        File defaultDir = new File(defaultPath);

        try {

            String normalizedSavePath = saveFile.getCanonicalPath();
            String normalizedDefaultPath = defaultDir.getCanonicalPath();

            return normalizedSavePath.startsWith(normalizedDefaultPath);
        } catch (IOException e) {

            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    private static void getPackageOrNativeFiles(DirectoryEntry directoryEntry) throws IOException {
        Iterator<Entry> directoryEntryIterator = directoryEntry.getEntries();

        while (directoryEntryIterator.hasNext()) {
            Entry entry = directoryEntryIterator.next();
            //log.info(entry.getName());

            if (entry.getName().equals(OleEntry.PACKAGE.getValue())) {
                DocumentEntry packageEntry = (DocumentEntry) directoryEntry.getEntry(OleEntry.PACKAGE.getValue());
                packageParser((DocumentEntry) directoryEntry.getEntry("\u0001CompObj"), packageEntry);
            }

            if (entry.getName().equals(Ole10Native.OLE10_NATIVE)) {
                DocumentEntry ole10Native = (DocumentEntry) directoryEntry.getEntry(Ole10Native.OLE10_NATIVE);

                //DocumentInputStream oleStream = new DocumentInputStream(ole10Native);
                Ole10NativeParser(new DocumentInputStream(ole10Native));
            }
        }
    }
    public static void getOleFiles(String pathFile, String fileType) {

        try (FileInputStream inputStream = new FileInputStream(pathFile)) {


            switch (fileType) {
                case "application/vnd.ms-powerpoint" -> {

                    HSLFSlideShow hslfSlideShow = new HSLFSlideShow(inputStream);
                    List<HSLFObjectData> objects = List.of(hslfSlideShow.getEmbeddedObjects());
                    for (HSLFObjectData object : objects) {
                        POIFSFileSystem fs = new POIFSFileSystem(object.getInputStream());

                        getPackageOrNativeFiles(fs.getRoot());
                        fs.close();
                    }
                    hslfSlideShow.close();
                }
                case "application/vnd.ms-excel" -> {
                    HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
                    for (HSSFObjectData hssfObjectData : hssfWorkbook.getAllEmbeddedObjects()) {

                        getPackageOrNativeFiles(hssfObjectData.getDirectory());

                    }
                    hssfWorkbook.close();
                }
                case "application/msword" -> {
                    HWPFDocumentCore hwpfDocument = new HWPFDocument(inputStream);

                    Iterator<Entry> hwpfDocumentIterator = hwpfDocument.getDirectory().getEntries();
                    while (hwpfDocumentIterator.hasNext()) {
                        Entry hwpfDocumententry = hwpfDocumentIterator.next();

                        if (hwpfDocumententry.getName().equals(OleEntry.OBJECTPOOL.getValue())) {
                            DirectoryEntry objectPool = (DirectoryEntry) hwpfDocument.getDirectory().getEntry(OleEntry.OBJECTPOOL.getValue());

                            Iterator<Entry> objectPoolEntries = objectPool.getEntries();
                            while (objectPoolEntries.hasNext()) {
                                Entry objectPoolentry = objectPoolEntries.next();

                                if (filePattern.matcher(objectPoolentry.getName()).matches()) {

                                    getPackageOrNativeFiles((DirectoryEntry) objectPool.getEntry(objectPoolentry.getName()));
                                }
                            }
                        }
                    }
                    hwpfDocument.close();
                }
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->{

                    XWPFDocument docx = new XWPFDocument(inputStream);
                    getParseFile(docx.getAllEmbeddedParts());
                    docx.close();
                }
                case "application/vnd.openxmlformats-officedocument.presentationml.presentation" ->{
                    XSLFSlideShow pptx = new XSLFSlideShow(OPCPackage.open(inputStream));
                    getParseFile(pptx.getAllEmbeddedParts());
                    pptx.close();
                }
                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ->{
                    XSSFWorkbook xlsx = new XSSFWorkbook(OPCPackage.open(inputStream));
                    getParseFile(xlsx.getAllEmbeddedParts());

                    xlsx.close();
                }
            }
        } catch (IOException | OpenXML4JException | XmlException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getParseFile(List<PackagePart> picture) throws IOException {
        for(int i=0; i<picture.size(); i++) {
            if(picture.get(i).getContentType().equals(MimeType.OLEOBJECT.getValue())){

                Ole10NativeParser(picture.get(i).getInputStream());
                continue;
            }

            Matcher matcher = DiractoryPattern.matcher(picture.get(i).getPartName().getName());

            if(matcher.find()){
                String fileNameAndExtension = matcher.group(0);

                try (FileOutputStream fileOutputStream = new FileOutputStream("C:\\files\\ole\\" + fileNameAndExtension)) {
                    fileOutputStream.write(picture.get(i).getInputStream().readAllBytes());
                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                }
            }
        }
    }
    private static void Ole10NativeParser(InputStream inputStream){

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            int footerSize = -1;
            int startPatternIndex = -1;  // 헤더 시작 위치를 기억하는 변수
            int endPatternIndex = -1;    // 푸터 시작 위치를 기억하는 변수

            // 검사할 파일 유형들의 헤더와 푸터 패턴을 배열로 정의
            byte[][] headerPatterns = new byte[][] {
                    pngStartPattern,
                    pdfStartPattern,
                    jpg1StartPattern,
                    jpg2StartPattern
            };

            byte[] footerPatterns = new byte[]{};

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);

                if (startPatternIndex == -1) {
                    for (byte[] headerPattern : headerPatterns) {

                        int headerIndex = indexOf(outputStream.toByteArray(), headerPattern);
                        if (headerIndex != -1) {
                            startPatternIndex = headerIndex;
                            // 파일 유형에 따라 확장자 설정
                            if (Arrays.equals(headerPattern, pngStartPattern)) {
                                fileTypeString = FileType.PNG.getValue();
                                footerSize = pngEndPattern.length;
                                footerPatterns = Arrays.copyOfRange(pngEndPattern, 0, footerSize);

                            } else if (Arrays.equals(headerPattern, pdfStartPattern)) {
                                fileTypeString = FileType.PDF.getValue();
                                footerSize = pdfEndPattern.length;
                                footerPatterns = Arrays.copyOfRange(pdfEndPattern, 0, footerSize);
                            } else if (Arrays.equals(headerPattern, jpg1StartPattern) || Arrays.equals(headerPattern, jpg2StartPattern)) {
                                fileTypeString = FileType.JPG.getValue();
                                footerSize = jpgEndPattern.length;
                                footerPatterns = Arrays.copyOfRange(jpgEndPattern, 0, footerSize);
                            } else {
                                fileTypeString = FileType.BIN.getValue();
                            }
                            break;
                        }
                    }
                }

                if(startPatternIndex != -1) {
                    int footerIndex = indexOf(outputStream.toByteArray(), footerPatterns, outputStream.size() - 1);
                    if (footerIndex != -1) {
                        endPatternIndex = footerIndex;

                        // 푸터를 찾았으니 파일 유형이 맞는지 검증
                        boolean isValidFooter = Arrays.equals(Arrays.copyOfRange(outputStream.toByteArray(), footerIndex, footerIndex + footerSize), footerPatterns);
                        if (!isValidFooter) {
                            log.error("푸터 유효성 검증 실패");
                            //endPatternIndex = -1; // 푸터 검증 실패 시 푸터 인덱스 초기화
                        }

                        break;
                    }
                }

            }
            inputStream.close();

            if (startPatternIndex != -1 && endPatternIndex != -1) {
                byte[] extractedData = Arrays.copyOfRange(outputStream.toByteArray(), startPatternIndex, endPatternIndex + footerSize);

                String outputFileName = String.format("%s.%s",  FileUtil.getRtNum(), fileTypeString);
                try (FileOutputStream fileOutputStream = new FileOutputStream("C:\\files\\ole\\" + outputFileName)) {
                    fileOutputStream.write(extractedData);
                    //log.info("OLE object saved to: C:\\files\\ole\\" + outputFileName);
                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                }
            } else {
                log.error("헤더 또는 푸터를 찾을 수 없음");
            }
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            log.error("IO 오류 발생");
        }
    }



    private static void packageParser(DocumentEntry compObj, DocumentEntry packageFileEntry) throws IOException{

        DocumentInputStream oleTypeStream = new DocumentInputStream(compObj);
        byte[] buffer = new byte[oleTypeStream.available()];
        oleTypeStream.read(buffer);
        oleTypeStream.close();

        String compObjContents = new String(buffer);

        if (compObjContents.contains(FileType.POWERPOINT.getValue())) {
            fileTypeString = FileType.PPTX.getValue();
        } else if (compObjContents.contains(FileType.EXCEL.getValue())) {
            fileTypeString = FileType.XLSX.getValue();
        } else if (compObjContents.contains(FileType.WORD.getValue())) {
            fileTypeString = FileType.DOCX.getValue();
        }

        DocumentInputStream oleStream = new DocumentInputStream(packageFileEntry);
        byte[] oleData = new byte[oleStream.available()];
        oleStream.read(oleData);
        oleStream.close();

        String outputFileName = String.format("%s.%s",  FileUtil.getRtNum(), fileTypeString);
        try (FileOutputStream outputStream = new FileOutputStream("C:\\files\\ole\\" + outputFileName)) {
            outputStream.write(oleData);
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            log.error("파일 저장 실패");
        }
    }

    private static String getRtNum() {
        int nSeed;
        int nSeedSize = 62; // 숫자 + 영문
        String strSrc = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; // 숫자, 영문
        StringBuilder strKey = new StringBuilder();
        for(int i=0; i<12; i++){
            nSeed = (int)(Math.random() * nSeedSize) + 1;
            strKey.append(strSrc.charAt(nSeed - 1));
        }
        return strKey.toString();
    }


    private static int indexOf(byte[] source, byte[] pattern) {
        for (int i = 0; i <= source.length - pattern.length; i++) {
            boolean match = true;
            for (int j = 0; j < pattern.length; j++) {
                if (source[i + j] != pattern[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }

    private static int indexOf(byte[] data, byte[] pattern, int endIndex) {
        int patternEndIndex = pattern.length - 1;

        for (int i = endIndex; i >= patternEndIndex; i--) {
            boolean match = true;
            for (int j = patternEndIndex; j >= 0; j--) {
                if (data[i - (patternEndIndex - j)] != pattern[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i - patternEndIndex;
            }
        }
        return -1;
    }

/*    private static int findPatternIndex(byte[] source, byte[] pattern) {
        for (int i = 0; i <= source.length - pattern.length; i++) {
            if (Arrays.equals(source, i, i + pattern.length, pattern, 0, pattern.length)) {
                return i;
            }
        }
        return -1;
    }*/
    /*    private static int indexOf(byte[] data, byte[] pattern, int endIndex) {
        for (int i = endIndex; i >= pattern.length - 1; i--) {
            boolean match = true;
            for (int j = 0; j < pattern.length; j++) {
                if (data[i - j] != pattern[pattern.length - j - 1]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }*/

 /*   private static int indexOf(byte[] data, byte[] pattern, int endIndex) {
        for (int i = endIndex - pattern.length + 1; i >= 0; i--) {
            boolean match = true;
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }*/

/*    private static int indexOf(byte[] data, byte[] pattern, int endIndex) {
        for (int i = endIndex - pattern.length + 1; i >= 0; i--) {
            boolean match = true;
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }*/

    /*    private static void Ole10NativeParser( DocumentEntry ole10Native){

        try (DocumentInputStream oleStream = new DocumentInputStream(ole10Native)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            int footerSize = -1;
            int startPatternIndex = -1;  // 헤더 시작 위치를 기억하는 변수
            int endPatternIndex = -1;    // 푸터 시작 위치를 기억하는 변수

            // 검사할 파일 유형들의 헤더와 푸터 패턴을 배열로 정의
            byte[][] headerPatterns = new byte[][] {
                    pngStartPattern,
                    pdfStartPattern,
                    jpg1StartPattern,
                    jpg2StartPattern
            };

            byte[] footerPatterns = new byte[]{};

            while ((bytesRead = oleStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);

                if (startPatternIndex == -1) {
                    for (byte[] headerPattern : headerPatterns) {
                        int headerIndex = indexOf(outputStream.toByteArray(), headerPattern);
                        if (headerIndex != -1) {
                            startPatternIndex = headerIndex;
                            // 파일 유형에 따라 확장자 설정
                            if (Arrays.equals(headerPattern, pngStartPattern)) {
                                fileTypeString = FileType.PNG.getValue();
                                footerSize = pngEndPattern.length;
                                footerPatterns = Arrays.copyOfRange(pngEndPattern, 0, footerSize);

                            } else if (Arrays.equals(headerPattern, pdfStartPattern)) {
                                fileTypeString = FileType.PDF.getValue();
                                footerSize = pdfEndPattern.length;
                                footerPatterns = Arrays.copyOfRange(pdfEndPattern, 0, footerSize);
                            } else if (Arrays.equals(headerPattern, jpg1StartPattern) || Arrays.equals(headerPattern, jpg2StartPattern)) {
                                fileTypeString = FileType.JPG.getValue();
                                footerSize = jpgEndPattern.length;
                                footerPatterns = Arrays.copyOfRange(jpgEndPattern, 0, footerSize);
                            } else {
                                fileTypeString = FileType.BIN.getValue();
                            }
                            break;
                        }
                    }
                }

                if(startPatternIndex != -1) {
                    int footerIndex = indexOf(outputStream.toByteArray(), footerPatterns, outputStream.size() - 1);
                    if (footerIndex != -1) {
                        endPatternIndex = footerIndex;

                        // 푸터를 찾았으니 파일 유형이 맞는지 검증
                        boolean isValidFooter = Arrays.equals(Arrays.copyOfRange(outputStream.toByteArray(), footerIndex, footerIndex + footerSize), footerPatterns);
                        if (!isValidFooter) {
                            log.error("푸터 유효성 검증 실패");
                            //endPatternIndex = -1; // 푸터 검증 실패 시 푸터 인덱스 초기화
                        }

                        break;
                    }
                }
            }

            if (startPatternIndex != -1 && endPatternIndex != -1) {
                byte[] extractedData = Arrays.copyOfRange(outputStream.toByteArray(), startPatternIndex, endPatternIndex + footerSize);

                String outputFileName = String.format("%s.%s",  FileUtil.getRtNum(), fileTypeString);
                try (FileOutputStream fileOutputStream = new FileOutputStream("C:\\files\\ole\\" + outputFileName)) {
                    fileOutputStream.write(extractedData);
                    //log.info("OLE object saved to: C:\\files\\ole\\" + outputFileName);
                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                }
            } else {
                log.error("헤더 또는 푸터를 찾을 수 없음");
            }
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            log.error("IO 오류 발생");
        }
    }*/

    private FileUtil() {
    }
}

