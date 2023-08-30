package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtil {


    private static String savePath = null;
    public static String fileName = null;

    public static String fileTypeString = null;
    public static final Pattern filePattern = Pattern.compile("_\\d{10}");
    public static final Pattern DiractoryPattern = Pattern.compile("([^/]+)\\.(\\w+)$");
    /*public static final Pattern EmbeddedFileName = Pattern.compile(":[\\\\/][^:]+\\.[A-Za-z0-9]+");
    private static final byte[] pngStartPattern = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
    private static final byte[] pngEndPattern = new byte[] { (byte) 0x49, 0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82};
    private static final byte[] jpg1StartPattern = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
    private static final byte[] jpg2StartPattern = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1 };
    private static final byte[] jpgEndPattern = new byte[] { (byte) 0xff, (byte)0xD9 };
    private static final byte[] pdfStartPattern = new byte[]{ (byte) 0x25, 0x50, 0x44, 0x46};
    private static final byte[] pdfEndPattern = new byte[]{ (byte) 0x25, 0x25, 0x45, 0x4F, 0x46};*/

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

            if (entry.getName().equals(OleEntry.PACKAGE.getValue())) {
                DocumentEntry packageEntry = (DocumentEntry) directoryEntry.getEntry(OleEntry.PACKAGE.getValue());
                packageParser((DocumentEntry) directoryEntry.getEntry("\u0001CompObj"), packageEntry);
            }

            if (entry.getName().equals(Ole10Native.OLE10_NATIVE)) {
                DocumentEntry ole10Native = (DocumentEntry) directoryEntry.getEntry(Ole10Native.OLE10_NATIVE);

                Ole10NativeParser(new DocumentInputStream(ole10Native));
            }
        }
    }
    public static List<String> getOleFiles(FileDto fileDto) {

        List<String> fileList = new ArrayList<>();
        savePath = fileDto.getFileOlePath();

        try (FileInputStream inputStream = new FileInputStream(fileDto.getFileSavePath())) {
            File Folder = new File(savePath);

            if(!Folder.exists()){
                try{
                    Folder.mkdir(); //폴더 생성합니다.
                }
                catch(Exception e){
                    ExceptionUtils.getStackTrace(e);
                }
            }

            switch (fileDto.getFileType()) {
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
            File[] files = Folder.listFiles();

            for(File file : Objects.requireNonNull(files)){
                fileList.add(savePath+"\\"+file.getName());
            }
        } catch (IOException | OpenXML4JException | XmlException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException(e);
        }

        return fileList;
    }

    private static void getParseFile(List<PackagePart> picture) throws IOException {
        for(int i=0; i<picture.size(); i++) {
            if(picture.get(i).getContentType().equals(MimeType.OLEOBJECT.getValue())){

                POIFSFileSystem poifs = new POIFSFileSystem(picture.get(i).getInputStream());
                DirectoryEntry root = poifs.getRoot();
                DocumentEntry ole10NativeEntry = (DocumentEntry)root.getEntry(Ole10Native.OLE10_NATIVE);

                InputStream oleInputStream = poifs.createDocumentInputStream(ole10NativeEntry.getName());
                Ole10NativeParser(oleInputStream);

                poifs.close();
                continue;
            }

            Matcher matcher = DiractoryPattern.matcher(picture.get(i).getPartName().getName());

            if(matcher.find()){
                String fileNameAndExtension = matcher.group(0);

                try (FileOutputStream fileOutputStream = new FileOutputStream(savePath +"\\"+ fileNameAndExtension)) {
                    fileOutputStream.write(picture.get(i).getInputStream().readAllBytes());
                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                }

            }
        }
    }
    private static void Ole10NativeParser(InputStream inputStream){

        try (ByteArrayOutputStream variableData = new ByteArrayOutputStream()){
            inputStream.skip(6);

            int byteRead;
            while ((byteRead = inputStream.read()) != -1) {
                if (byteRead == 0x00) {
                    break;
                }
                variableData.write(byteRead);
            }
            fileName = variableData.toString(Charset.forName("euc-kr"));
            //log.info(fileName);

            while (true) {
                byteRead = inputStream.read();
                if (byteRead == -1) {
                    break;
                }
                variableData.write(byteRead);
                if (variableData.size() >= 5
                        && variableData.toByteArray()[variableData.size() - 5] == 0x00
                        && variableData.toByteArray()[variableData.size() - 4] == 0x00
                        && variableData.toByteArray()[variableData.size() - 3] == 0x00
                        && variableData.toByteArray()[variableData.size() - 2] == 0x03
                        && variableData.toByteArray()[variableData.size() - 1] == 0x00) {
                    //log.info("0x 00 00 00 03 00발견!!");
                    break;
                }
            }

            byte[] tempPathSizeBytes = new byte[4];
            inputStream.read(tempPathSizeBytes);

            int dataSize = (tempPathSizeBytes[3] & 0xFF) << 24 |
                    (tempPathSizeBytes[2] & 0xFF) << 16 |
                    (tempPathSizeBytes[1] & 0xFF) << 8 |
                    (tempPathSizeBytes[0] & 0xFF);

            inputStream.skip(dataSize);

            byte[] embeddedDataSize = new byte[4];
            inputStream.read(embeddedDataSize);
            int realSize = (embeddedDataSize[3] & 0xFF) << 24 |
                    (embeddedDataSize[2] & 0xFF) << 16 |
                    (embeddedDataSize[1] & 0xFF) << 8 |
                    (embeddedDataSize[0] & 0xFF);


            byte[] embeddedData = new byte[realSize];
            inputStream.read(embeddedData);

            File outputFile = new File(savePath +"\\"+ fileName);
            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                fileOutputStream.write(embeddedData);
                //System.out.println("File saved: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
            }


            /*byte[] buffer = new byte[1024];
            int bytesRead;*/


            /*int footerSize = -1;
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

                Matcher matcher = EmbeddedFileName.matcher(outputStream.toString("euc-kr").replaceAll("\\s", ""));

                if(matcher.find()){
                    String[] pathParts = matcher.group().substring(1).split("[\\\\/]");
                    fileName = pathParts[pathParts.length - 1];
                }

                if (startPatternIndex == -1) {
                    for (byte[] headerPattern : headerPatterns) {

                        int headerIndex = indexOf(outputStream.toByteArray(), headerPattern);
                        if (headerIndex != -1) {
                            startPatternIndex = headerIndex;
                            // 파일 유형에 따라 확장자 설정
                            if (Arrays.equals(headerPattern, pngStartPattern)) {
                                footerSize = pngEndPattern.length;
                                footerPatterns = Arrays.copyOfRange(pngEndPattern, 0, footerSize);
                            } else if (Arrays.equals(headerPattern, pdfStartPattern)) {
                                footerSize = pdfEndPattern.length;
                                footerPatterns = Arrays.copyOfRange(pdfEndPattern, 0, footerSize);
                            } else if (Arrays.equals(headerPattern, jpg1StartPattern) || Arrays.equals(headerPattern, jpg2StartPattern)) {
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
                        }
                        break;
                    }
                }
            }
            inputStream.close();

            if (startPatternIndex != -1 && endPatternIndex != -1) {
                byte[] extractedData = Arrays.copyOfRange(outputStream.toByteArray(), startPatternIndex, endPatternIndex + footerSize);

                if(fileName == null){
                    String fileName = String.format("%s.%s",  FileUtil.getRtNum(), fileTypeString);
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(savePath +"\\"+ fileName)) {
                    fileOutputStream.write(extractedData);
                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                }
            } else {
                log.error("헤더 또는 푸터를 찾을 수 없음");
            }*/
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
        try (FileOutputStream outputStream = new FileOutputStream(savePath +"\\"+ outputFileName)) {
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


    private FileUtil() {
    }
}

