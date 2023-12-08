package com.example.fileUpload.documentParser.module;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.*;
import org.springframework.stereotype.Component;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;


@Slf4j
@NoArgsConstructor
@Component
public class EmbeddedFileExtractor {

    private static final int SIZE_OF_SEPARATOR = 1;

    public void parserOleNativeEntry(DirectoryNode directoryNode, String fileOlePath){
        StringBuffer stringBuffer = new StringBuffer();
        FileOutputStream fileOutputStream =null;
        BufferedOutputStream bufferedOutputStream = null;

        try{
            Ole10Native fromEmbeddedOleObject = Ole10Native.createFromEmbeddedOleObject(directoryNode);

            String originalFileName = getOriginalFileName(fromEmbeddedOleObject);
            String uuid = addUniqueFileNameMapping(originalFileName);

            stringBuffer.append(fileOlePath).append(File.separator).append(uuid);
            fileOutputStream = new FileOutputStream(stringBuffer.toString());
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(fromEmbeddedOleObject.getDataBuffer());


        } catch (IOException | Ole10NativeException e) {
            ExceptionUtils.getStackTrace(e);
            log.info("스트림을 처리하는 과정에서 예외가 발생하였습니다.");

        }finally{
            IOUtils.closeQuietly(bufferedOutputStream);
            IOUtils.closeQuietly(fileOutputStream);
            stringBuffer.setLength(0);
        }
    }
    //TODO CompObj로 변경 예정
    public String parseFileType(DocumentEntry compObj){

        DocumentInputStream compObjStream = null;

        String fileFormat;
        String fileTypeString;
        String fileType="other";

        try{

            compObjStream = new DocumentInputStream(compObj);

            compObjStream.skipNBytes(28);

            //공통 부분 찾기 1
            byte[] fileNameSizeBytes = new byte[4];
            compObjStream.readFully(fileNameSizeBytes);
            ByteBuffer fileNameSize = ByteBuffer.wrap(fileNameSizeBytes).order(ByteOrder.LITTLE_ENDIAN);
            byte[] fileNameData = new byte[fileNameSize.getInt()];
            compObjStream.readFully(fileNameData);
            fileFormat = new String(fileNameData, Charset.forName("euc-kr")).trim();



            //공통 부분 찾기 2
            byte[] skipSizeBytes = new byte[4];
            compObjStream.readFully(skipSizeBytes);
            ByteBuffer skipSize = ByteBuffer.wrap(skipSizeBytes).order(ByteOrder.LITTLE_ENDIAN);


            //이것만 skip 기능임
            compObjStream.skipNBytes(skipSize.getInt());



            //공통 부분 찾기 3
            byte[] fileTypeBytes = new byte[4];
            compObjStream.readFully(fileTypeBytes);
            ByteBuffer fileTypeSize = ByteBuffer.wrap(skipSizeBytes).order(ByteOrder.LITTLE_ENDIAN);
            byte[] fileTypeData = new byte[fileTypeSize.getInt()];
            compObjStream.readFully(fileTypeData);
            fileTypeString = new String(fileTypeData, Charset.forName("euc-kr"));


            if (fileTypeString.startsWith("Excel.Sheet.12") || fileFormat.equals("Microsoft Excel Worksheet")) {
                fileType=".xlsx";
                //return ".xlsx";
            }
            if (fileTypeString.startsWith("Word.Document.12") || fileFormat.equals("Microsoft Word Document")) {
                fileType = ".docx";
            }
            if (fileTypeString.startsWith("PowerPoint.Show.12") || fileFormat.equals("Microsoft PowerPoint Presentation")) {
                fileType=".pptx";
            }
            if (fileTypeString.startsWith("PowerPoint.OpenDocumentPresentation.12")) {
                fileType=".odp";
            }
            if (fileTypeString.startsWith("Excel.OpenDocumentSpreadsheet.12")) {
                fileType=".ods";
            }
            if (fileTypeString.startsWith("Word.OpenDocumentText.12")) {
                fileType=".odt";
            }
            if(fileTypeString.startsWith("PBrush")){
                fileType=".bmp";
            }
            if(fileTypeString.startsWith("Excel.SheetMacroEnabled.12") || fileFormat.startsWith("Microsoft Excel Macro-Enabled")){
                fileType=".csv";
            }


        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        }finally {
            IOUtils.closeQuietly(compObjStream);
        }
        return  fileType;
    }

    private String getOriginalFileName(Ole10Native fromEmbeddedOleObject) throws Ole10NativeException, IOException {
        String oleFileName = fromEmbeddedOleObject.getFileName();
        int index = oleFileName.lastIndexOf(File.separator);

        return oleFileName.substring(index+SIZE_OF_SEPARATOR);
    }

}
