package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.FileType;
import com.example.fileUpload.unit.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.springframework.stereotype.Component;

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

    public static void parsePackageEntry(DocumentEntry compObj, DocumentEntry packageFileEntry, String fileOlePath) throws IOException {
        DocumentInputStream oleTypeStream = new DocumentInputStream(compObj);
        byte[] buffer = new byte[oleTypeStream.available()];
        oleTypeStream.read(buffer);
        oleTypeStream.close();

        String compObjContents = new String(buffer);
        String fileTypeString=null;

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

        try (FileOutputStream outputStream = new FileOutputStream(fileOlePath +"\\"+ FileUtil.getRtNum()+fileTypeString)) {
            outputStream.write(oleData);
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            log.error("파일 저장 실패");
        }
    }
}
