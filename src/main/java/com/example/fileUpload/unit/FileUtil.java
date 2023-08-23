package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.*;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtil {

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

    public static void docOleParser(String pathFile) throws IOException{

        String regex = "_\\d{10}";
        Pattern pattern = Pattern.compile(regex);
        int fileCounter = 1;
        byte[] oleData;
        String fileTypeString = null;

        byte[] pngStartPattern = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
        byte[] pngEndPattern = new byte[] { (byte) 0x49, 0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82};
        byte[] jpgStartPattern = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
        byte[] jpgEndPattern = new byte[] { (byte) 0xff, (byte)0xD9 };


        //doc에 한해 구현한 코드 doc to (doc,ppt,xls,jpg,png)
        try(POIFSFileSystem fs = new POIFSFileSystem(new File(pathFile))) {
            DirectoryEntry root = fs.getRoot();

            DirectoryEntry objectPoolDir = (DirectoryEntry) root.getEntry("ObjectPool");

            for (Entry entryDir : objectPoolDir) {

                if (entryDir instanceof DirectoryEntry) {
                    String entryFileName = entryDir.getName();
                    Matcher matcher = pattern.matcher(entryFileName);

                    if (matcher.matches()) {
                        //System.out.println("Matching entry found: " + entryFileName + "  int : " + fileCounter);
                        DirectoryEntry randomNum = (DirectoryEntry) objectPoolDir.getEntry(entryFileName);

                        Iterator<Entry> entries = randomNum.getEntries();

                        while (entries.hasNext()) {
                            Entry entry = entries.next();
                            if (entry instanceof DocumentEntry dataEntry) {

                                String entryName = dataEntry.getName();
                                //System.out.println(entryName);
                                if (entryName.startsWith("Package")) {
                                    DocumentEntry fileEntry = (DocumentEntry) randomNum.getEntry(entryName);
                                    DocumentEntry oleType = (DocumentEntry) randomNum.getEntry("\u0001CompObj");

                                    DocumentInputStream oleTypeStream = new DocumentInputStream(oleType);
                                    byte[] buffer = new byte[oleTypeStream.available()];
                                    oleTypeStream.read(buffer);
                                    oleTypeStream.close();

                                    String compObjContents = new String(buffer);

                                    if (compObjContents.contains("PowerPoint")) {
                                        fileTypeString = "pptx";
                                    } else if (compObjContents.contains("Excel")) {
                                        fileTypeString = "xlsx";
                                    } else if (compObjContents.contains("Word")) {
                                        fileTypeString = "docx";
                                    }

                                    DocumentInputStream oleStream = new DocumentInputStream(fileEntry);
                                    oleData = new byte[oleStream.available()];
                                    oleStream.read(oleData);
                                    oleStream.close();

                                    String outputFileName = String.format("output-ole-object%d.%s", fileCounter, fileTypeString);
                                    try (FileOutputStream outputStream = new FileOutputStream("C:\\files\\ole\\" + outputFileName)) {
                                        outputStream.write(oleData);
                                    } catch (IOException e) {
                                        ExceptionUtils.getStackTrace(e);
                                        log.error("파일 저장 실패");
                                    }


                                } else if (entryName.endsWith(Ole10Native.OLE10_NATIVE)) {

                                    DocumentEntry fileEntry = (DocumentEntry) randomNum.getEntry(entryName);

                                    try (DocumentInputStream oleStream = new DocumentInputStream(fileEntry)) {
                                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                        byte[] buffer = new byte[1024];
                                        int bytesRead;

                                        boolean pngIsLocation = false;
                                        boolean jpgIsLocation = false;

                                        int startPatternIndex = -1;  // 헤더 시작 위치를 기억하는 변수
                                        int endPatternIndex = -1;    // 푸터 시작 위치를 기억하는 변수

                                        while ((bytesRead = oleStream.read(buffer)) != -1) {
                                            outputStream.write(buffer, 0, bytesRead);

                                            //png
                                            if (startPatternIndex == -1) {
                                                startPatternIndex = indexOf(outputStream.toByteArray(), pngStartPattern);
                                            }

                                            if (endPatternIndex == -1) {
                                                endPatternIndex = indexOf(outputStream.toByteArray(), pngEndPattern);
                                            }

                                            //jpg
                                            if (startPatternIndex == -1) {
                                                startPatternIndex = indexOf(outputStream.toByteArray(), jpgStartPattern);
                                            }

                                            if (endPatternIndex == -1) {
                                                endPatternIndex = indexOf(outputStream.toByteArray(), jpgEndPattern);
                                            }

                                            if (startPatternIndex != -1 && endPatternIndex != -1) {
                                                if (endPatternIndex > startPatternIndex) {
                                                    // Header and footer positions found, determine the file type
                                                    if (Arrays.equals(Arrays.copyOfRange(outputStream.toByteArray(), startPatternIndex, startPatternIndex + pngStartPattern.length), pngStartPattern)) {
                                                        fileTypeString = "png";
                                                    } else if (Arrays.equals(Arrays.copyOfRange(outputStream.toByteArray(), startPatternIndex, startPatternIndex + jpgStartPattern.length), jpgStartPattern)) {
                                                        fileTypeString = "jpg";
                                                    }

                                                    break;
                                                } else {
                                                    // Reset positions if end comes before start
                                                    startPatternIndex = -1;
                                                    endPatternIndex = -1;
                                                }
                                            }
                                        }

                                        if (startPatternIndex != -1 && endPatternIndex != -1) {
                                            // Extract the data between start and end patterns
                                            byte[] extractedData = Arrays.copyOfRange(outputStream.toByteArray(), startPatternIndex, endPatternIndex + pngEndPattern.length);

                                            // 데이터를 파일로 저장
                                            String outputFileName = String.format("output-ole-object%d.%s", fileCounter, fileTypeString);
                                            try (FileOutputStream fileOutputStream = new FileOutputStream("C:\\files\\ole\\" + outputFileName)) {
                                                fileOutputStream.write(extractedData);

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
                            }
                        }
                        fileCounter++;
                    }
                }
            }
        }
    }

    public static List<String> getFolderFiles(String folderPath){
        File folder = new File(folderPath);
        String[] fileList = folder.list();
        return List.of(fileList);
    }

    public static void exploreDirectory(DirectoryEntry dirEntry) {
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
    }
    private static boolean startsWith(byte[] array, byte[] prefix) {
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

    private FileUtil() {
    }
}

