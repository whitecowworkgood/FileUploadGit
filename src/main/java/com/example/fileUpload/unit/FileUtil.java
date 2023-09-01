package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;
import kr.dogfoot.hwplib.object.bindata.BinData;
import kr.dogfoot.hwplib.object.bindata.EmbeddedBinaryData;
import kr.dogfoot.hwplib.reader.HWPReader;
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
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

@Slf4j
public class FileUtil {


    private static String savePath = null;
    public static String fileName = null;

    public static String fileTypeString = null;
    //public static final Pattern filePattern = Pattern.compile("_\\d{10}");
    public static final Pattern DiractoryPattern = Pattern.compile("([^/]+)\\.(\\w+)$");

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

    private static void selectOleFile(DirectoryEntry directoryEntry) throws IOException {

        if (directoryEntry.hasEntry(OleEntry.PACKAGE.getValue())) {
            DocumentEntry packageEntry = (DocumentEntry) directoryEntry.getEntry(OleEntry.PACKAGE.getValue());
            packageParser((DocumentEntry) directoryEntry.getEntry(OleEntry.COMPOBJ.getValue()), packageEntry);

        } else if (directoryEntry.hasEntry(Ole10Native.OLE10_NATIVE)) {
            DocumentEntry ole10Native = (DocumentEntry) directoryEntry.getEntry(Ole10Native.OLE10_NATIVE);
            Ole10NativeParser(new DocumentInputStream(ole10Native));
            }
    }
    /*
    여기가 서비스나 컨트롤러에 사용되는 큰 범주의 메서드
     */
    public static List<String> getOleFiles(FileDto fileDto) {

        List<String> fileList = new ArrayList<>();
        savePath = fileDto.getFileOlePath();
        File[] files;

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

                        selectOleFile(fs.getRoot());
                        fs.close();
                    }
                    hslfSlideShow.close();
                }
                case "application/vnd.ms-excel" -> {
                    HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
                    for (HSSFObjectData hssfObjectData : hssfWorkbook.getAllEmbeddedObjects()) {

                        selectOleFile(hssfObjectData.getDirectory());

                    }
                    hssfWorkbook.close();
                }
                case "application/msword" -> {
                    HWPFDocumentCore hwpfDocument = new HWPFDocument(inputStream);

                    hwpfDocument.getDirectory().hasEntry(OleEntry.OBJECTPOOL.getValue());

                        if (hwpfDocument.getDirectory().hasEntry(OleEntry.OBJECTPOOL.getValue())) {
                            DirectoryEntry objectPool = (DirectoryEntry) hwpfDocument.getDirectory().getEntry(OleEntry.OBJECTPOOL.getValue());

                            for (Iterator<Entry> it = objectPool.getEntries(); it.hasNext(); ) {
                                Entry entry = it.next();
                                selectOleFile((DirectoryEntry) objectPool.getEntry(entry.getName()));
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
                case "application/octet-stream" -> {
                    log.info("한컴 대기중");
                    POIFSFileSystem poifs = new POIFSFileSystem(inputStream);
                    DirectoryEntry root = poifs.getRoot();
                    DirectoryEntry binData = (DirectoryEntry)root.getEntry("BinData");

                    System.out.println(binData.getEntryNames());


                    //구현해야 하는 코드 POIFS로 HWP를 읽어서 암호화를 해제하는 코드 구현해 보기
                   /* Entry bin = binData.getEntry("BIN0004.OLE");

                    //스트림 바로 저장하는 코드를 확인차 추가함.
                    try (FileOutputStream fileOutputStream = new FileOutputStream(savePath +"\\"+ bin.getName())) {
                        fileOutputStream.write(zlibDecompression(bin.));
                    } catch (IOException e) {
                        ExceptionUtils.getStackTrace(e);
                        log.error("파일 저장 실패");
                    }*/


                    //--------------- zlib로 압축 해제 코드 짜보기-------------

                    /*BinData hwpFile = HWPReader.fromInputStream(inputStream).getBinData();

                    for(EmbeddedBinaryData data:hwpFile.getEmbeddedBinaryDataList()){

                        if(data.getName().endsWith(".OLE")){
                            log.info("{}에서 사용된 압축방법: {}",data.getName(),data.getCompressMethod());

                            //hwp에서 OLE 추출하는 코드
                            //hwpParser(new ByteArrayInputStream(data.getData()));

                            //zlibDecompression(data.getData());
                            //스트림 바로 저장하는 코드를 확인차 추가함.
                            try (FileOutputStream fileOutputStream = new FileOutputStream(savePath +"\\"+ data.getName())) {
                                fileOutputStream.write(data.getData());
                            } catch (IOException e) {
                                ExceptionUtils.getStackTrace(e);
                                log.error("파일 저장 실패");
                            }
                        }
                    }*/
                }
            }
            files = Folder.listFiles();

            for(File file : Objects.requireNonNull(files)){
                fileList.add(savePath+"\\"+file.getName());
            }
        } catch (IOException | OpenXML4JException | XmlException e) {
            ExceptionUtils.getStackTrace(e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return fileList;
    }


    private static void writeOleToFile(){

    }
    private static void hwpParser(InputStream inputStream){
        try {
            // 앞의 4바이트를 제외하고 남은 데이터를 POI 읽기
            inputStream.skip(4);// 앞의 4바이트를 건너뜀
            POIFSFileSystem pof = new POIFSFileSystem(inputStream);
            inputStream.close();

            DirectoryEntry root = pof.getRoot();

            if(root.hasEntry("WordDocument")){
                log.info("이 파일은 97-03버전의 Document");

                //Ole, OlePrev000등을 삭제하는 코드 -> 삭제가 불필요하면 제거하기
                List<Entry> entriesToDelete = new ArrayList<>();
                Iterator<Entry> entries = root.getEntries();
                while (entries.hasNext()) {
                    Entry entry = entries.next();
                    if (entry.getName().startsWith("Ole", 1)) {
                        //System.out.println(entry.getName() + " 삭제 대상");
                        entriesToDelete.add(entry);
                    }
                }

                for (Entry entry : entriesToDelete) {
                    //System.out.println(entry.getName() + " 삭제");
                    root.getEntry(entry.getName()).delete();
                }

                FileOutputStream fos = new FileOutputStream(savePath+"\\"+getRtNum()+FileType.DOC.getValue());
                pof.writeFilesystem(fos);

                fos.close();

            }else if(root.hasEntry("PowerPoint Document")){
                log.info("이 파일은 97-03버전의 ppt");
                //Ole, OlePrev000등을 삭제하는 코드 -> 삭제가 불필요하면 제거하기
                List<Entry> entriesToDelete = new ArrayList<>();
                Iterator<Entry> entries = root.getEntries();
                while (entries.hasNext()) {
                    Entry entry = entries.next();
                    if (entry.getName().startsWith("Ole", 1)) {
                        //System.out.println(entry.getName() + " 삭제 대상");
                        entriesToDelete.add(entry);
                    }
                }

                for (Entry entry : entriesToDelete) {
                    //System.out.println(entry.getName() + " 삭제");
                    root.getEntry(entry.getName()).delete();
                }

                FileOutputStream fos = new FileOutputStream(savePath+"\\"+getRtNum()+FileType.PPT.getValue());
                pof.writeFilesystem(fos);

                fos.close();

            } else if (root.hasEntry("Workbook")) {
                log.info("이 파일은 97-03버전의 xlsx");
                //Ole, OlePrev000등을 삭제하는 코드 -> 삭제가 불필요하면 제거하기
                List<Entry> entriesToDelete = new ArrayList<>();
                Iterator<Entry> entries = root.getEntries();
                while (entries.hasNext()) {
                    Entry entry = entries.next();
                    if (entry.getName().startsWith("Ole", 1)) {
                        //System.out.println(entry.getName() + " 삭제 대상");
                        entriesToDelete.add(entry);
                    }
                }

                for (Entry entry : entriesToDelete) {
                    //System.out.println(entry.getName() + " 삭제");
                    root.getEntry(entry.getName()).delete();
                }

                FileOutputStream fos = new FileOutputStream(savePath+"\\"+getRtNum()+FileType.XLS.getValue());
                pof.writeFilesystem(fos);

                fos.close();

            } else if (root.hasEntry(Ole10Native.OLE10_NATIVE)) {
                DocumentEntry ole10Native = (DocumentEntry) root.getEntry(Ole10Native.OLE10_NATIVE);
                Ole10NativeParser(new DocumentInputStream(ole10Native));
            }else{
                log.info("Embedded 추출이 지원이 안되는 파일(ex: docx)파일이 존재합니다.");
            }

            pof.close();
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
        }

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

        //String outputFileName = String.format("%s.%s",  FileUtil.getRtNum(), fileTypeString);
        try (FileOutputStream outputStream = new FileOutputStream(savePath +"\\"+ FileUtil.getRtNum()+fileTypeString)) {
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

    public static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 서브폴더인 경우 재귀 호출로 서브폴더 삭제
                        deleteFolder(file);
                    } else {
                        // 파일인 경우 삭제
                        file.delete();
                    }
                }
            }
        }
    }

    public static byte[] zlibDecompression(byte[] inputStream) throws IOException {

        // 압축 해제
        Inflater inflater = new Inflater();
        inflater.setInput(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        try (InflaterInputStream inflaterInputStream = new InflaterInputStream(new ByteArrayInputStream(inputStream))) {
            while ((bytesRead = inflaterInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] decompressedData = outputStream.toByteArray();

        //log.info(Arrays.toString(decompressedData));
        return decompressedData;
    }

    private FileUtil() {
    }
}

