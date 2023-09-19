package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.XOfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@NoArgsConstructor
@Slf4j
public class XWordParser extends FileParser {


    @Override
    public void parse(FileDto fileDto) throws IOException, OpenXML4JException {

        Map<PackagePart, PackagePart> packagePartMap = new HashMap<>();

        FileInputStream fs = null;
        XWPFDocument docx = null;
        XOfficeEntryHandler xOfficeEntryHandler = new XOfficeEntryHandler();

        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            docx = new XWPFDocument(OPCPackage.open(fs));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // XML 데이터 파싱
            Document document = builder.parse(new InputSource(new StringReader(docx.getDocument().getBody().toString())));

            // "w:object" 태그의 "o:OLEObject" 태그 하위에서 "r:id"와 "DrawAspect" 속성 값을 출력
            NodeList objectNodes = document.getElementsByTagName("w:object");

            for (int i = 0; i < objectNodes.getLength(); i++) {
                Element objectElement = (Element) objectNodes.item(i);
                NodeList oleObjectNodes = objectElement.getElementsByTagName("o:OLEObject");
                NodeList shapeNodes = objectElement.getElementsByTagName("v:shape");

                for (int j = 0; j < oleObjectNodes.getLength(); j++) {
                    Element oleObjectElement = (Element) oleObjectNodes.item(j);
                    String drawAspectValue = oleObjectElement.getAttribute("DrawAspect");

                    // "v:shape" 태그 선택 (o:OLEObject 하위)
                    Element shapeNodeElement = (Element) shapeNodes.item(j);
                    String ridValue = oleObjectElement.getAttribute("r:id");

                    // "v:shape" 태그 내의 "v:imagedata" 태그 선택
                    NodeList imagedataNodes = shapeNodeElement.getElementsByTagName("v:imagedata");
                    String imageRidValue = null;

                    for (int k = 0; k < imagedataNodes.getLength(); k++) {
                        Element imagedataElement = (Element) imagedataNodes.item(k);
                        imageRidValue = imagedataElement.getAttribute("r:id");
                    }

                    System.out.println("r:id value: " + ridValue);
                    System.out.println("DrawAspect = " + drawAspectValue);
                    System.out.println("Image r:id value: " + imageRidValue);

                    System.out.println(docx.getPictureDataByID(imageRidValue).suggestFileExtension());

                    if(drawAspectValue.equals("Icon")
                            && docx.getPictureDataByID(imageRidValue).getData().length>7000
                            && docx.getPictureDataByID(imageRidValue).suggestFileExtension().equals("emf")){
                        System.out.println("emf파일이 4156바이트가 넘고, Icon형식이고고, ");
                    }
                }
            }

            //xOfficeEntryHandler.parser(docx.getPartById(ridValue), fileDto.getOriginFileName(), fileDto.getFileOlePath());
            /*for (PackagePart pPart : docx.getAllEmbeddedParts()) {

                //잠시 주석처리
                //xOfficeEntryHandler.parser(pPart, fileDto.getOriginFileName(), fileDto.getFileOlePath());
            }*/

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(docx);
        }
    }
}
