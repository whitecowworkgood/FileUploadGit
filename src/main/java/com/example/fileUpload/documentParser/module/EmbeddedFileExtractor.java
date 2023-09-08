package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.FileType;
import com.example.fileUpload.unit.FileUtil;
import com.example.fileUpload.unit.OleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;


@Slf4j
public class EmbeddedFileExtractor {
    static StringBuilder stringBuilder = new StringBuilder();
    public static void parseOle10NativeEntry(InputStream inputStream, String fileOlePath) {

        try (ByteArrayOutputStream variableData = new ByteArrayOutputStream()){
            inputStream.skipNBytes(6);

            int byteRead;
            while ((byteRead = inputStream.read()) != -1) {
                if (byteRead == 0x00) {
                    break;
                }
                variableData.write(byteRead);
            }
            String fileName = variableData.toString(Charset.forName("euc-kr"));
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

            inputStream.skipNBytes(dataSize);

            byte[] embeddedDataSize = new byte[4];
            inputStream.read(embeddedDataSize);

            int realSize = (embeddedDataSize[3] & 0xFF) << 24 |
                    (embeddedDataSize[2] & 0xFF) << 16 |
                    (embeddedDataSize[1] & 0xFF) << 8 |
                    (embeddedDataSize[0] & 0xFF);


            byte[] embeddedData = new byte[realSize];
            inputStream.read(embeddedData);

            stringBuilder.append(fileOlePath).append(File.separator).append(fileName);
            try (FileOutputStream fileOutputStream = new FileOutputStream(stringBuilder.toString())) {
                fileOutputStream.write(embeddedData);
            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
            }
            stringBuilder.setLength(0);

        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            log.error("IO 오류 발생");
        }finally {
            IOUtils.closeQuietly(inputStream);
        }

    }
    public static String parseFileName(DocumentEntry compObj){
        String fileFormat=null;
        String fileTypeString=null;
        String fileType=null;

        try(DocumentInputStream compObjStream = new DocumentInputStream(compObj)){

            compObjStream.skipNBytes(28);

            byte[] fileNameSizeBytes = new byte[4];
            compObjStream.readFully(fileNameSizeBytes);

            int fileNameSize = (fileNameSizeBytes[3] & 0xFF) << 24 |
                    (fileNameSizeBytes[2] & 0xFF) << 16 |
                    (fileNameSizeBytes[1] & 0xFF) << 8 |
                    (fileNameSizeBytes[0] & 0xFF);

            byte[] fileNameData = new byte[fileNameSize];
            compObjStream.readFully(fileNameData);

            fileFormat = FileUtil.removeNullCharacters(new String(fileNameData, Charset.forName("euc-kr")));


            //---------상단으로 파일명 확인------------

            byte[] skipSizeBytes = new byte[4];
            compObjStream.readFully(skipSizeBytes);

            int skipSize = (skipSizeBytes[3] & 0xFF) << 24 |
                    (skipSizeBytes[2] & 0xFF) << 16 |
                    (skipSizeBytes[1] & 0xFF) << 8 |
                    (skipSizeBytes[0] & 0xFF);

            compObjStream.skipNBytes(skipSize);

            byte[] fileTypeBytes = new byte[4];
            //compObjStream.read(fileTypeBytes);
            compObjStream.readFully(fileTypeBytes);

            int fileTypeSize = (fileTypeBytes[3] & 0xFF) << 24 |
                    (fileTypeBytes[2] & 0xFF) << 16 |
                    (fileTypeBytes[1] & 0xFF) << 8 |
                    (fileTypeBytes[0] & 0xFF);


            byte[] fileTypeData = new byte[fileTypeSize];
            compObjStream.readFully(fileTypeData);


            fileTypeString = new String(fileTypeData, Charset.forName("euc-kr"));


            //log.info(fileTypeString);
            if(fileTypeString.startsWith("Excel.SheetMacroEnabled")){
                fileType = ".csv";
            } else if (fileTypeString.startsWith("Word.DocumentMacroEnabled")) {
                fileType = ".docm";
            } else if (fileTypeString.startsWith("PowerPoint.ShowMacroEnabled")) {
                fileType=".pptm";
            } else if (fileTypeString.startsWith("Excel.Sheet.12") || fileFormat.equals("Microsoft Excel Worksheet")) {
                fileType=".xlsx";
            } else if (fileTypeString.startsWith("Word.Document.12") || fileFormat.equals("Microsoft Word Document")) {
                fileType = ".docx";
            } else if (fileTypeString.startsWith("PowerPoint.Show.12") || fileFormat.equals("Microsoft PowerPoint Presentation")) {
                fileType=".pptx";
            } else if (fileTypeString.startsWith("Excel.Sheet.8")) {
                fileType=".xls";
            } else if (fileTypeString.startsWith("Word.Document.8")) {
                fileType=".doc";
            } else if (fileTypeString.startsWith("PowerPoint.Show.8")) {
                fileType =".ppt";
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String returnValue = stringBuilder.append(FileUtil.getRtNum()).append(fileType).toString();
        stringBuilder.setLength(0);
        return returnValue;
    }
    public static void parsePackageEntry(String fileName, DocumentEntry packageFileEntry, String fileOlePath) throws IOException {

        try(DocumentInputStream oleStream = new DocumentInputStream(packageFileEntry)){
            byte[] oleData = new byte[oleStream.available()];
            oleStream.readFully(oleData);

            stringBuilder.append(fileOlePath).append(File.separator).append(fileName);

            try (FileOutputStream outputStream = new FileOutputStream(stringBuilder.toString())) {
                outputStream.write(oleData);
            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            }
            stringBuilder.setLength(0);
        }catch(IOException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException(e);
        }

    }
    public static void parsePackageEntry(DocumentEntry packageFileEntry, String fileOlePath) throws IOException {

        try(DocumentInputStream oleStream = new DocumentInputStream(packageFileEntry)){
            byte[] oleData = new byte[oleStream.available()];
            oleStream.readFully(oleData);

            stringBuilder.append(fileOlePath).append(File.separator).append(FileUtil.getRtNum()).append(FileType.DOCX.getValue());
            try (FileOutputStream outputStream = new FileOutputStream(stringBuilder.toString())) {
                outputStream.write(oleData);
            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            }
            stringBuilder.setLength(0);
        }catch(IOException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException(e);
        }

    }


    public static String copyDirectory(DirectoryEntry sourceDir, DirectoryEntry destinationDir, String directoryName) throws IOException {
        // 기존 코드
        DirectoryEntry copyDir;

        String fileName = null;

        if (!sourceDir.getName().equals(directoryName)) {
            if (destinationDir.hasEntry(sourceDir.getName())) {
                copyDir = (DirectoryEntry) destinationDir.getEntry(sourceDir.getName());
            } else {
                copyDir = destinationDir.createDirectory(sourceDir.getName());
            }
        }else{
            copyDir=destinationDir;
        }

        // 하위 엔트리를 탐색하면서 복사
        Iterator<Entry> entries = sourceDir.getEntries();
        while (entries.hasNext()) {
            Entry entry = entries.next();
            if (entry.isDirectoryEntry()) {
                // 디렉터리 엔트리인 경우, 재귀적으로 복사
                DirectoryEntry sourceSubDir = (DirectoryEntry) entry;
                copyDirectory(sourceSubDir, copyDir, "");
            } else if (entry.isDocumentEntry()) {

                if(entry.getName().equals(OleEntry.COMPOBJ.getValue())){
                    fileName=parseFileName((DocumentEntry) entry);
                }
                // 문서 엔트리인 경우, 스트림 데이터를 읽어서 복사본에 생성 또는 업데이트
                try(InputStream sourceDocEntry = new DocumentInputStream((DocumentEntry) entry)){
                    DocumentEntry copyDocEntry = copyDir.createDocument(entry.getName(), sourceDocEntry);
                }catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    throw new RuntimeException(e);
                }
            }
        }
        return fileName;
    }

}
