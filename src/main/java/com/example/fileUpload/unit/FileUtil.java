package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.poifs.filesystem.*;

import java.io.*;
import java.util.*;

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

    public static void fileOleParser(String pathFile) throws IOException, OpenXML4JException {
        //doc

        try(POIFSFileSystem fs = new POIFSFileSystem(new File(pathFile))){
            DirectoryEntry root = fs.getRoot();
            DirectoryEntry objectPoolDir = (DirectoryEntry) root.getEntry("ObjectPool");
            //해당 엔트리는 doc에서 숫자가 랜덤임 정규표현식이나 기타 방법을 써야함.
            DirectoryEntry randomNum = (DirectoryEntry) objectPoolDir.getEntry("_1754246191");
            DocumentEntry PackageFile = (DocumentEntry) randomNum.getEntry("Package");

            DocumentInputStream stream = new DocumentInputStream(PackageFile);

            //System.out.println(stream);
            int dataSize = PackageFile.getSize(); // 읽을 데이터 크기

            byte[] buffer = new byte[dataSize];
            int bytesRead = stream.read(buffer, 0, dataSize);

            if (bytesRead > 0) {
                String data = new String(buffer, 0, bytesRead);
                System.out.println("Read data: " + data);
            } else {
                System.out.println("No data read.");
            }
            //System.out.println(PackageFile);
            //listDirectoryContents(objectPoolDir, "");

            /*for(Entry entry: root){
                System.out.println("dir: " + entry.getName());
                //System.out.println(entry.);
            }*/

        }
        /*try (FileInputStream fis = new FileInputStream(pathFile);
             POIFSFileSystem poifs = new POIFSFileSystem(fis);
             HWPFDocument document = new HWPFDocument(poifs)) {

            ObjectPool objectPool = document.getObjectsPool();
            int objectCount = objectPool.getObjectCount();

            for (int i = 0; i < objectCount; i++) {
                String objectName = objectPool.getObject(i).getName();
                String className = objectPool.getObject(i).getClassName();
                long size = objectPool.getObject(i).getSize();

                System.out.println("Object Name: " + objectName);
                System.out.println("Class Name: " + className);
                System.out.println("Size: " + size + " bytes");
                System.out.println("---------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /*try (FileInputStream fis = new FileInputStream(pathFile);
             POIFSFileSystem poifs = new POIFSFileSystem(fis);
             HWPFDocument document = new HWPFDocument(poifs)) {

            Picture[] pictures = document.getPicturesTable().getAllPictures().toArray(new Picture[0]);
            for (Picture picture : pictures) {
                String objectName = picture.suggestFullFileName();
                System.out.println("Object Name: " + objectName);

                byte[] bytes = picture.getContent();
                picture.suggestFileExtension();
                System.out.println("Content: " + Arrays.toString(bytes));

                // 추출된 데이터(bytes)를 원하는 방식으로 처리합니다.
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //}


        /*FileInputStream fis = new FileInputStream(pathFile);

        XWPFDocument document = new XWPFDocument(fis);

        List<PackagePart> embeddedDocs;
        embeddedDocs = document.getAllEmbeddedParts();

        if (embeddedDocs != null && !embeddedDocs.isEmpty()) {

            for (PackagePart pPart : embeddedDocs) {
                System.out.print(pPart.getPartName().getName() + ", ");
                System.out.print(pPart.getContentType() + ", ");
                System.out.println();
                //System.out.println(pPart.save(getPartName());
            }
            //docx는 됨
            //추후 생성자를 바꿔서 doc와 ppt파일들, 엑셀도 되게 하기
            *//*
            네, 주어진 코드는 .docx 확장자를 가진 파일인 DOCX 형식의 문서에서 embedded 객체를 추출하는 코드입니다. XWPFDocument 클래스는 Apache POI 라이브러리에서 제공하는 클래스 중 하나로, Word 문서를 처리할 때 사용되며 주로 DOCX 파일 형식을 다룰 때 사용됩니다.

따라서 이 코드는 .docx 확장자를 가진 파일의 내부에 있는 embedded 객체들의 정보를 출력하는 목적으로 작성된 것입니다. 위의 코드는 OOXML 형식의 DOCX 파일에서만 작동합니다. 만약 .doc 확장자의 문서를 처리하려면, 다른 형식의 문서를 다루는 코드를 사용해야 할 수 있습니다.

.doc 확장자를 가진 문서를 처리하기 위해서는 HWPFDocument 클래스를 사용하여야 합니다. 해당 클래스는 .doc 확장자를 가진 문서의 내용을 추출하는 데 사용됩니다. 이와 비슷하게 .ppt 확장자를 가진 프레젠테이션 파일이 있다면, .pptx 형식의 프레젠테이션을 처리하는 XSLFSlideShow 클래스를 사용하여 추출 작업을 수행할 수 있습니다.

요약하자면, 코드는 .docx 확장자를 가진 파일의 embedded 객체 정보를 출력하는 데 사용되며, 다른 확장자의 문서를 다루려면 해당 확장자에 맞는 라이브러리와 클래스를 사용하여 처리해야 합니다.
             *//*
        }*/
    }

/*        List<String> oleObjectList = new ArrayList<>(); // OLE 정보를 담을 리스트

        File documentFile = new File(pathFile);

        try (InputStream stream = new FileInputStream(documentFile)) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();

            parser.parse(stream, handler, metadata, context);

            String[] oleObjectInfo = metadata.getValues("Workbook");
            if (oleObjectInfo != null && oleObjectInfo.length > 0) {
                System.out.println("OLE Object Information: " + oleObjectInfo[0]);

            }else{
                log.info("no ole");
            }
            //return oleObjectInfo;
        } catch (TikaException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }*/

        /*try (FileInputStream fis = new FileInputStream(pathFile)) {
            XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(fis));
            List<XWPFPicture> pictures = document.getAllPictures();

            for (XWPFPicture picture : pictures) {
                XmlCursor cursor = picture.getCTPicture().newCursor();
                cursor.selectPath("./*");

                while(cursor.toNextSelection()) {
                    if (cursor.getName().getLocalPart().equals("imagedata")) {
                        String oleFilename = cursor.getAttributeText(new QName("ole", "filename"));
                        System.out.println("OLE Object Filename: " + oleFilename);
                    }
                }
            }
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
        }*/

    //}

    public static List<String> getFolderFiles(String folderPath){
        File folder = new File(folderPath);
        String[] fileList = folder.list();
        return List.of(fileList);
    }

    public static void listDirectoryContents(DirectoryEntry dir, String indent) {
        for (Entry entry : dir) {
            if (entry instanceof DirectoryEntry) {
                System.out.println(indent + "Dir: " + entry.getName());
                listDirectoryContents((DirectoryEntry) entry, indent + "  ");
            } else if (entry instanceof DocumentEntry) {
                System.out.println(indent + "File: " + entry.getName());
            }
        }
    }



    private FileUtil() {
    }
}

