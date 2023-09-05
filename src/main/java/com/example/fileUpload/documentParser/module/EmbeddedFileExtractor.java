package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.FileType;
import com.example.fileUpload.unit.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import java.io.*;
import java.nio.charset.Charset;


@Slf4j
public class EmbeddedFileExtractor {

    public static void parseOle10NativeEntry(InputStream inputStream, String fileOlePath) {

        try (ByteArrayOutputStream variableData = new ByteArrayOutputStream()){
            inputStream.skip(6);

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

            inputStream.skip(dataSize);

            byte[] embeddedDataSize = new byte[4];
            inputStream.read(embeddedDataSize);

            int realSize = (embeddedDataSize[3] & 0xFF) << 24 |
                    (embeddedDataSize[2] & 0xFF) << 16 |
                    (embeddedDataSize[1] & 0xFF) << 8 |
                    (embeddedDataSize[0] & 0xFF);


            byte[] embeddedData = new byte[realSize];
            inputStream.read(embeddedData);

            //File outputFile = new File(savePath +"\\"+ fileName);
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOlePath +"\\"+ fileName)) {
                fileOutputStream.write(embeddedData);
                //System.out.println("File saved: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
            }

        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            log.error("IO 오류 발생");
        }
    }
    public static String parseFileName(DocumentEntry compObj){
        String fileName=null;
        String fileTypeString=null;
        String fileType=null;

        try(DocumentInputStream compObjStream = new DocumentInputStream(compObj)){

            compObjStream.skip(28);

            byte[] fileNameSizeBytes = new byte[4];
            compObjStream.read(fileNameSizeBytes);

            int fileNameSize = (fileNameSizeBytes[3] & 0xFF) << 24 |
                    (fileNameSizeBytes[2] & 0xFF) << 16 |
                    (fileNameSizeBytes[1] & 0xFF) << 8 |
                    (fileNameSizeBytes[0] & 0xFF);

            byte[] fileNameData = new byte[fileNameSize];
            compObjStream.read(fileNameData);

            fileName = FileUtil.removeNullCharacters(new String(fileNameData, Charset.forName("euc-kr")));


            //---------상단으로 파일명 확인------------

            byte[] skipSizeBytes = new byte[4];
            compObjStream.read(skipSizeBytes);

            int skipSize = (skipSizeBytes[3] & 0xFF) << 24 |
                    (skipSizeBytes[2] & 0xFF) << 16 |
                    (skipSizeBytes[1] & 0xFF) << 8 |
                    (skipSizeBytes[0] & 0xFF);

            //fileTypeStringServ = FileUtil.removeNullCharacters(new String(skipSizeBytes, Charset.forName("euc-kr")));


            compObjStream.skip(skipSize);

            byte[] fileTypeBytes = new byte[4];
            compObjStream.read(fileTypeBytes);

            int fileTypeSize = (fileTypeBytes[3] & 0xFF) << 24 |
                    (fileTypeBytes[2] & 0xFF) << 16 |
                    (fileTypeBytes[1] & 0xFF) << 8 |
                    (fileTypeBytes[0] & 0xFF);


            byte[] fileTypeData = new byte[fileTypeSize];
            compObjStream.read(fileTypeData);


            fileTypeString = new String(fileTypeData, Charset.forName("euc-kr"));


            //log.info(fileTypeString);
            if(fileTypeString.startsWith("Excel.SheetMacroEnabled")){
                fileType = ".csv";
            } else if (fileTypeString.startsWith("Word.DocumentMacroEnabled")) {
                fileType = ".docm";
            } else if (fileTypeString.startsWith("PowerPoint.ShowMacroEnabled")) {
                fileType=".pptm";
            } else if (fileTypeString.startsWith("Excel.Sheet.12") || fileName.equals("Microsoft Excel Worksheet")) {
                fileType=".xlsx";
            } else if (fileTypeString.startsWith("Word.Document.12") || fileName.equals("Microsoft Word Document")) {
                fileType = ".docx";
            } else if (fileTypeString.startsWith("PowerPoint.Show.12") || fileName.equals("Microsoft PowerPoint Presentation")) {
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
        return fileName+fileType;
    }
    public static void parsePackageEntry(String fileName, DocumentEntry packageFileEntry, String fileOlePath) throws IOException {

        DocumentInputStream oleStream = new DocumentInputStream(packageFileEntry);
        byte[] oleData = new byte[oleStream.available()];
        oleStream.read(oleData);
        oleStream.close();

        log.info(fileName);

        try (FileOutputStream outputStream = new FileOutputStream(fileOlePath +"\\"+fileName)) {
            outputStream.write(oleData);
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            log.error("파일 저장 실패");
        }
    }
    public static void parsePackageEntry(DocumentEntry packageFileEntry, String fileOlePath) throws IOException {

        DocumentInputStream oleStream = new DocumentInputStream(packageFileEntry);
        byte[] oleData = new byte[oleStream.available()];
        oleStream.read(oleData);
        oleStream.close();



        try (FileOutputStream outputStream = new FileOutputStream(fileOlePath +"\\"+FileUtil.getRtNum()+FileType.DOCX.getValue())) {
            outputStream.write(oleData);
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            log.error("파일 저장 실패");
        }
    }
}
