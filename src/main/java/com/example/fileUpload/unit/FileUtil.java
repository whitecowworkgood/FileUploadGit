package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.record.ExOleObjStg;
import org.apache.poi.hslf.usermodel.HSLFObjectData;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.sl.usermodel.ObjectData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtil {

    public static String fileTypeString = null;

    public static final Pattern xlsPattern = Pattern.compile("MBD[A-Z0-9]{8}");
    public static final Pattern filePattern = Pattern.compile("_\\d{10}");
    public static final Pattern docPattern = Pattern.compile("ObjectPool");
    public static final Pattern pptPattern = Pattern.compile("PowerPoint Document");

    public static boolean valuedDocFile(FileDto fileDto){
        List<String> validTypeList = List.of("application/pdf","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "application/msword");

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


    public static void oleParser(String pathFile, String fileType) {//ole를 추출하는 제일 첫번째 코드, 정규표현식 메서드 이용 후, 반환된 리스트로

        List<String> legacyFileTypes = List.of(
                        "application/vnd.ms-excel", "application/msword");

        List<String> modernFileTypes = List.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        int fileCounter=0;

        if(fileType.equals("application/vnd.ms-powerpoint")){

            try (FileInputStream inputStream = new FileInputStream(pathFile)) {
                List<HSLFObjectData> objects;

                HSLFSlideShow ppt = new HSLFSlideShow(inputStream);

                objects = List.of(ppt.getEmbeddedObjects());

                for(HSLFObjectData object: objects){
                    POIFSFileSystem fs = new POIFSFileSystem(object.getInputStream());
                    DirectoryEntry directoryEntry = fs.getRoot();

                    Iterator<Entry> entryIterator = directoryEntry.getEntries();
                    
                    while (entryIterator.hasNext()) {
                        Entry entry = entryIterator.next();

                        if (entry.getName().startsWith("Package")) {
                            DocumentEntry packageEntry = (DocumentEntry) directoryEntry.getEntry("Package");
                            packageParser((DocumentEntry) directoryEntry.getEntry("\u0001CompObj"), packageEntry, fileCounter+1);
                        }

                        if (entry.getName().endsWith("Ole10Native")) {
                            DocumentEntry ole10Native = (DocumentEntry) directoryEntry.getEntry(Ole10Native.OLE10_NATIVE);
                            Ole10NativeParser(ole10Native, fileCounter+1);
                        }
                    }
                    fileCounter++;
                    fs.close();
                }
                ppt.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (fileType.equals("application/vnd.ms-excel")) {
            try (FileInputStream inputStream = new FileInputStream(pathFile)) {
                HSSFWorkbook xls = new HSSFWorkbook(inputStream);

                System.out.println("Embedding object: "+xls.getAllEmbeddedObjects());

                for(HSSFObjectData entry: xls.getAllEmbeddedObjects()){
                    System.out.println(entry.getDirectory().getEntryNames());
                    //mbd엔트리 안까지 접근 완료, 이제 ppt랑똑같이 구별하는 기능 추가
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else if (fileType.equals("application/vnd.ms-msword")) {
            //doc파일 추출 관련 코드 추가하기

        }/*else if (legacyFileTypes.contains(fileType)) {
            //나중에 xls를 따로 뽑아내기
            try (POIFSFileSystem fs = new POIFSFileSystem(new File(pathFile))) {

                //기존에 동작하는 코드
                List<DirectoryEntry> result = exploreDirectoryFiles(fs.getRoot());

                for (DirectoryEntry ent : result) {
                    System.out.println(ent.getEntryNames());

                    Iterator<Entry> entryIterator = ent.getEntries();
                    while (entryIterator.hasNext()) {
                        Entry entry = entryIterator.next();

                        if (entry.getName().startsWith("Package")) {
                            DocumentEntry packageEntry = (DocumentEntry) ent.getEntry("Package");
                            packageParser((DocumentEntry) ent.getEntry("\u0001CompObj"), packageEntry, fileCounter+1);
                        }

                        if (entry.getName().endsWith("Ole10Native")) {
                            DocumentEntry ole10Native = (DocumentEntry) ent.getEntry(Ole10Native.OLE10_NATIVE);
                            Ole10NativeParser(ole10Native, fileCounter+1);

                        }
                    }
                    fileCounter++;
                }

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
            }
            
        }*/ else if (modernFileTypes.contains(fileType)) {
            log.info("아직 미구현");
        }

    }
    //여기서 값들을 리스트로 저장 후, 상단의 코드로 넘겨서, 구현 예정
    public static List<DirectoryEntry> exploreDirectoryFiles(DirectoryEntry directory) throws FileNotFoundException {

        List<DirectoryEntry> directoryEntryList = new ArrayList<>();

        for (Entry entry : directory) {
            String entryName = entry.getName();
            Matcher xlsMatcher = FileUtil.xlsPattern.matcher(entryName);
            Matcher docMatcher = FileUtil.docPattern.matcher(entryName);
            Matcher pptMatcher = FileUtil.pptPattern.matcher(entryName);


            if (xlsMatcher.matches()) {
                DirectoryEntry mbd = (DirectoryEntry) directory.getEntry(entryName);
                directoryEntryList.add(mbd);

            } else if (docMatcher.matches()) {
                DirectoryEntry objectPool = (DirectoryEntry) directory.getEntry(entryName);

                for (Entry secondEntry : objectPool) {
                    String secondEntryName = secondEntry.getName();
                    Matcher fileMatcher = FileUtil.filePattern.matcher(secondEntryName);

                    if (fileMatcher.matches()) {
                        DirectoryEntry randomNum = (DirectoryEntry) objectPool.getEntry(secondEntryName);
                        directoryEntryList.add(randomNum);
                    }
                }
            } else if (pptMatcher.matches()) {
                System.out.println("Found powerpoint: " + entryName);
                System.out.println("하지만 구현하진 않음, ppt는 ole가 있는지도 검증하기 어려움");
            }
        }

        /*for(DirectoryEntry ent: directoryEntryList){
            System.out.println(ent.getName());
        }*/
        return directoryEntryList;
    }

    public static void Ole10NativeParser( DocumentEntry ole10Native, int fileCounter){
        byte[] pngStartPattern = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
        byte[] pngEndPattern = new byte[] { (byte) 0x49, 0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82};
        byte[] jpgStartPattern = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
        byte[] jpgEndPattern = new byte[] { (byte) 0xff, (byte)0xD9 };
        byte[] pdfStartPattern = new byte[]{ (byte) 0x25, 0x50, 0x44, 0x46};
        byte[] pdfEndPattern = new byte[]{ (byte) 0x25, 0x25, 0x45, 0x4F, 0x46};

        //entryName.endsWith(Ole10Native.OLE10_NATIVE);

           // DocumentEntry fileEntry = (DocumentEntry) randomNum.getEntry(entryName);

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
                        jpgStartPattern
                };

                byte[] footerPatterns = new byte[]{};

                while ((bytesRead = oleStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    if (startPatternIndex == -1) {
                        for (int i = 0; i < headerPatterns.length; i++) {
                            int headerIndex = indexOf(outputStream.toByteArray(), headerPatterns[i]);
                            if (headerIndex != -1) {
                                startPatternIndex = headerIndex;
                                // 파일 유형에 따라 확장자 설정
                                if (Arrays.equals(headerPatterns[i], pngStartPattern)) {
                                    fileTypeString = FileType.PNG.getValue();
                                    footerSize=pngEndPattern.length;
                                    footerPatterns = Arrays.copyOfRange(pngEndPattern, 0, footerSize);

                                } else if (Arrays.equals(headerPatterns[i], pdfStartPattern)) {
                                    fileTypeString = FileType.PDF.getValue();
                                    footerSize=pdfEndPattern.length;
                                    footerPatterns = Arrays.copyOfRange(pdfEndPattern, 0, footerSize);
                                }else if (Arrays.equals(headerPatterns[i], jpgStartPattern)) {
                                    fileTypeString = FileType.JPG.getValue();
                                    footerSize=jpgEndPattern.length;
                                    footerPatterns = Arrays.copyOfRange(jpgEndPattern, 0, footerSize);
                                } else{
                                    fileTypeString=FileType.BIN.getValue();
                                }
                                break;
                            }
                        }
                    }
                    //새로 구현한 코드
                    if (endPatternIndex == -1 && startPatternIndex != -1) {
                        int footerIndex = indexOf(outputStream.toByteArray(), footerPatterns, outputStream.size() - 1);
                        if (footerIndex != -1) {
                            endPatternIndex = footerIndex;

                            // 푸터를 찾았으니 파일 유형이 맞는지 검증
                            boolean isValidFooter = Arrays.equals(Arrays.copyOfRange(outputStream.toByteArray(), footerIndex, footerIndex + footerSize), footerPatterns);
                            if (!isValidFooter) {
                                log.error("푸터 유효성 검증 실패");
                                endPatternIndex = -1; // 푸터 검증 실패 시 푸터 인덱스 초기화
                            }

                            break;
                        }
                    }

                    if (startPatternIndex != -1 && endPatternIndex != -1) {
                        break;
                    }
                }

                if (startPatternIndex != -1 && endPatternIndex != -1) {
                    byte[] extractedData = Arrays.copyOfRange(outputStream.toByteArray(), startPatternIndex, endPatternIndex + footerSize);

                    String outputFileName = String.format("output-ole-object%d.%s", fileCounter, fileTypeString);
                    try (FileOutputStream fileOutputStream = new FileOutputStream("C:\\files\\ole\\" + outputFileName)) {
                        fileOutputStream.write(extractedData);
                        log.info("OLE object saved to: C:\\files\\ole\\" + outputFileName);
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


    public static void packageParser(DocumentEntry compObj, DocumentEntry packageFileEntry, int fileCounter) throws IOException{

        DocumentInputStream oleTypeStream = new DocumentInputStream(compObj);
        byte[] buffer = new byte[oleTypeStream.available()];
        oleTypeStream.read(buffer);
        oleTypeStream.close();

        String compObjContents = new String(buffer);

        if (compObjContents.contains("PowerPoint")) {
            fileTypeString = FileType.PPTX.getValue();
        } else if (compObjContents.contains("Excel")) {
            fileTypeString = FileType.XLSX.getValue();
        } else if (compObjContents.contains("Word")) {
            fileTypeString = FileType.DOCX.getValue();
        }

        DocumentInputStream oleStream = new DocumentInputStream(packageFileEntry);
        byte[] oleData = new byte[oleStream.available()];
        oleStream.read(oleData);
        oleStream.close();

        String outputFileName = String.format("output-ole-object%d.%s", fileCounter, fileTypeString);
        try (FileOutputStream outputStream = new FileOutputStream("C:\\files\\ole\\" + outputFileName)) {
            outputStream.write(oleData);
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            log.error("파일 저장 실패");
        }
    }



/*
    public static List<String> getFolderFiles(String folderPath){
        File folder = new File(folderPath);
        String[] fileList = folder.list();
        return List.of(fileList);
    }
*/

/*    public static void exploreDirectory(DirectoryEntry dirEntry) {
        System.out.println("Directory: " + dirEntry.getName());

        Iterator<Entry> entries = dirEntry.getEntries();
        while (entries.hasNext()) {
            Entry entry = entries.next();
            if (entry instanceof DirectoryEntry) {
                exploreDirectory((DirectoryEntry) entry); // 재귀 호출로 하위 디렉토리 탐색
            } else {
                System.out.println("File: " + entry.getName());
            }
        }
    }*/
   /* private static boolean startsWith(byte[] array, byte[] prefix) {
        if (array.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (array[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean endsWith(byte[] data, byte[] endPattern) {
        if (data.length < endPattern.length) {
            return false;
        }

        for (int i = 0; i < endPattern.length; i++) {
            if (data[data.length - endPattern.length + i] != endPattern[i]) {
                return false;
            }
        }

        return true;
    }*/

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


    private FileUtil() {
    }
}

