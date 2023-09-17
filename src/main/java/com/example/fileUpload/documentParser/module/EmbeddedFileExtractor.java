package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.util.FileUtil;
import com.example.fileUpload.util.OleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;


import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;


@Slf4j
public class EmbeddedFileExtractor {
    static StringBuilder stringBuilder = new StringBuilder();

    /**
     * 문서내 저장된 기타 파일(이미지, pdf등등)을 추출합니다.
     *
     * @param fileOlePath 추출된 ole파일을 저장할 폴더경로
     * @param inputStream 업로드 된 문서의 Ole10Native를 Inputstream으로 가져옴.
     * */
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


            String uuid = addUniqueFileNameMapping(fileName);

            stringBuilder.append(fileOlePath).append(File.separator).append(uuid);
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
    /**
     * 문서내 연결된 문서파일의 이름과 확장자를 반환합니다.
     *
     * @param compObj 문서내 연결된 파일에 있는 타입을 확인할 수 있는 Entry데이터 입니다.
     * @return 파일명을 추출합니다.
     * */
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
            compObjStream.readFully(fileTypeBytes);

            int fileTypeSize = (fileTypeBytes[3] & 0xFF) << 24 |
                    (fileTypeBytes[2] & 0xFF) << 16 |
                    (fileTypeBytes[1] & 0xFF) << 8 |
                    (fileTypeBytes[0] & 0xFF);


            byte[] fileTypeData = new byte[fileTypeSize];
            compObjStream.readFully(fileTypeData);


            fileTypeString = new String(fileTypeData, Charset.forName("euc-kr"));


            if (fileTypeString.startsWith("Excel.Sheet.12") || fileFormat.equals("Microsoft Excel Worksheet")) {
                fileType=".xlsx";
            } else if (fileTypeString.startsWith("Word.Document.12") || fileFormat.equals("Microsoft Word Document")) {
                fileType = ".docx";
            }else if (fileTypeString.startsWith("PowerPoint.Show.12") || fileFormat.equals("Microsoft PowerPoint Presentation")) {
                fileType=".pptx";
            }else if (fileTypeString.startsWith("PowerPoint.OpenDocumentPresentation.12")) {
                fileType=".odp";
            }else if (fileTypeString.startsWith("Excel.OpenDocumentSpreadsheet.12")) {
                fileType=".ods";
            }else if (fileTypeString.startsWith("Word.OpenDocumentText.12")) {
                fileType=".odt";
            }else if(fileTypeString.startsWith("PBrush")){
                fileType=".bmp";
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*String returnValue = stringBuilder.append(fileFormat).append(fileType).toString();
        stringBuilder.setLength(0);*/
        return fileType;
    }

    /**
     * 문서내 ole문서의 데이터를 추출하여, 저장합니다.
     *
     * @param fileOlePath 추출된 ole 객체의 저장경로
     * @param fileName  추출된 ole 객체의 파일명
     * @param packageFileEntry 데이터를 추출하기 위한 Package라는  Entry 데이터
     *
     *
     * */
    public static void parsePackageEntry(String fileName, DocumentEntry packageFileEntry, String fileOlePath) throws IOException {
        DocumentInputStream oleStream =null;
        XSSFWorkbook workbook =null;
        byte[] oleData = null;
        try {
            oleStream = new DocumentInputStream(packageFileEntry);

            if(FileUtil.getFileExtension(fileName).equals(".xlsx")){

                workbook = new XSSFWorkbook(new ByteArrayInputStream(oleStream.readAllBytes()));

                CTBookView[] cb = workbook.getCTWorkbook().getBookViews().getWorkbookViewArray();

                cb[0].setVisibility(STVisibility.VISIBLE);
                workbook.getCTWorkbook().getBookViews().setWorkbookViewArray(cb);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);

                oleData = outputStream.toByteArray();
            }else{
                oleData = new byte[oleStream.available()];
                oleStream.readFully(oleData);
            }

            String uuid = addUniqueFileNameMapping(fileName);

            stringBuilder.append(fileOlePath).append(File.separator).append(uuid);


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
        }finally {
            IOUtils.closeQuietly(oleStream);
            IOUtils.closeQuietly(workbook);
        }

    }
    /**
     * docx파일 추출시, compObj이라는 엔트리가 없어서 추가한 코드
     *
     * @param fileOlePath 추출된 ole 객체의 저장경로
     * @param packageFileEntry 데이터를 추출하기 위한 Package라는  Entry 데이터
     *
     * */
    public static void parsePackageEntry(DocumentEntry packageFileEntry, String fileOlePath) throws IOException {

        try(DocumentInputStream oleStream = new DocumentInputStream(packageFileEntry)){
            byte[] oleData = new byte[oleStream.available()];
            oleStream.readFully(oleData);
            String fileName = "Microsoft Word 문서.docx"; //임시로 넣어놓은 코드

            String uuid = addUniqueFileNameMapping(fileName);

            stringBuilder.append(fileOlePath).append(File.separator).append(uuid);

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

    /**
     * 97-03파일에 97-03버전의 문서가 포함되어 있으면, Package라는 엔트리가 아니라 별도의 Directory Entry에 저장되기에 추출합니다.
     *
     * @param destinationDir 저장할 문서의 Directory Entry를 지정합니다.
     * @param directoryName Directory Entry를 지정하여, 처음 Entry Name을 무시합니다.
     * @param sourceDir Directory Entry를 지정합니다.
     *
     * @return 추출된 문서의 이름을 반환합니다.
     * */
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
