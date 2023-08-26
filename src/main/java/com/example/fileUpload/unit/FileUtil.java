package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.usermodel.HSLFObjectData;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwmf.usermodel.HwmfEmbedded;
import org.apache.poi.hwmf.usermodel.HwmfEmbeddedIterator;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.poifs.filesystem.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtil {
    public static String fileTypeString = null;
    public static final Pattern filePattern = Pattern.compile("_\\d{10}");

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

    public static void getOleFiles(DirectoryEntry directoryEntry) throws IOException {
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
                Ole10NativeParser(ole10Native);
            }
        }
    }
    public static void oleParser(String pathFile, String fileType) {

        try (FileInputStream inputStream = new FileInputStream(pathFile)) {

            switch (fileType) {
                case "application/vnd.ms-powerpoint" -> {
                    List<HSLFObjectData> objects;
                    HSLFSlideShow hslfSlideShow = new HSLFSlideShow(inputStream);
                    objects = List.of(hslfSlideShow.getEmbeddedObjects());
                    for (HSLFObjectData object : objects) {
                        POIFSFileSystem fs = new POIFSFileSystem(object.getInputStream());

                        getOleFiles(fs.getRoot());
                        fs.close();
                    }
                    hslfSlideShow.close();
                }
                case "application/vnd.ms-excel" -> {
                    HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
                    for (HSSFObjectData hssfObjectData : hssfWorkbook.getAllEmbeddedObjects()) {

                        getOleFiles(hssfObjectData.getDirectory());

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

                                    getOleFiles((DirectoryEntry) objectPool.getEntry(objectPoolentry.getName()));
                                }
                            }
                        }
                    }
                    hwpfDocument.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void Ole10NativeParser( DocumentEntry ole10Native){

        byte[] pngStartPattern = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
        byte[] pngEndPattern = new byte[] { (byte) 0x49, 0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82};
        byte[] jpg1StartPattern = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
        byte[] jpg2StartPattern = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1 };
        byte[] jpgEndPattern = new byte[] { (byte) 0xff, (byte)0xD9 };
        byte[] pdfStartPattern = new byte[]{ (byte) 0x25, 0x50, 0x44, 0x46};
        byte[] pdfEndPattern = new byte[]{ (byte) 0x25, 0x25, 0x45, 0x4F, 0x46};
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
                    byte[] fullData = outputStream.toByteArray();
                    log.info(String.valueOf(fullData.length));
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

                    //잠시 주석 처리 상단에 있는 코드를 테스트해야 함.
                     if(startPatternIndex != -1) {
                         //원래는 indexOf임
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

    }

    public static void packageParser(DocumentEntry compObj, DocumentEntry packageFileEntry) throws IOException{

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

    public static String getRtNum() {
        int nSeed = 0;
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

    private static int indexOf(byte[] data, byte[] pattern, int endIndex) {
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
    }

    //기존 코드
    /*private static int indexOf(byte[] data, byte[] pattern, int endIndex) {
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


    private FileUtil() {
    }
}

