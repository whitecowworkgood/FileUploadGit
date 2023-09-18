package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.util.FileUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;


import java.io.*;
import java.nio.charset.Charset;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;


@Slf4j
@NoArgsConstructor
public class EmbeddedFileExtractor {
    static StringBuilder stringBuilder = new StringBuilder();

    /**
     * 문서내 저장된 기타 파일(이미지, pdf등등)을 추출합니다.
     *
     * @param fileOlePath 추출된 ole파일을 저장할 폴더경로
     * @param inputStream 업로드 된 문서의 Ole10Native를 Inputstream으로 가져옴.
     * */
    public void parseOle10NativeEntry(InputStream inputStream, String fileOlePath) {
        ByteArrayOutputStream variableData =null;
        try{
            variableData = new ByteArrayOutputStream();

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
        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        }finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(variableData);
        }
    }
    /**
     * 문서내 연결된 문서파일의 이름과 확장자를 반환합니다.
     *
     * @param compObj 문서내 연결된 파일에 있는 타입을 확인할 수 있는 Entry데이터 입니다.
     * @return 파일명을 추출합니다.
     * */
    public String parseFileType(DocumentEntry compObj){
        DocumentInputStream compObjStream = null;

        String fileFormat=null;
        String fileTypeString=null;
        String fileType=null;

        try{
            compObjStream = new DocumentInputStream(compObj);

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
        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        }finally {
            IOUtils.closeQuietly(compObjStream);
        }
        return  fileType;
    }
}
