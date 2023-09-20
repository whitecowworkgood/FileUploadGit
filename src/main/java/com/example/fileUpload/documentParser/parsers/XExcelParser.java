package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.XOfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

@NoArgsConstructor
public class XExcelParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws IOException, OpenXML4JException {

        FileInputStream fs = null;
        XSSFWorkbook xlsx = null;
        XOfficeEntryHandler xOfficeEntryHandler = new XOfficeEntryHandler();
        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            xlsx = new XSSFWorkbook(OPCPackage.open(fs));


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            //System.out.println(xlsx.getSheetAt(3).getCTWorksheet().getOleObjects().toString());

            for (int i = 0; i < xlsx.getNumberOfSheets(); i++) {
                // System.out.println(xlsx.getSheetAt(i).getCTWorksheet().toString());

                InputSource inputSource = new InputSource(new StringReader(xlsx.getSheetAt(i).getCTWorksheet().toString()));
                Document document = builder.parse(inputSource);

                NodeList oleObjectNodes = document.getElementsByTagName("main:oleObject");
                String oleObjectId = null;
                String objectPrId = null;
                for (int j = 0; j < oleObjectNodes.getLength(); j++) {
                    Element oleObjectElement = (Element) oleObjectNodes.item(j);
                    oleObjectId = oleObjectElement.getAttribute("r:id");
                    //System.out.println("oleObject r:id: " + oleObjectId);

                    NodeList objectPrNodes = oleObjectElement.getElementsByTagName("main:objectPr");
                    for (int k = 0; k < objectPrNodes.getLength(); k++) {
                        Element objectPrElement = (Element) objectPrNodes.item(k);
                        objectPrId = objectPrElement.getAttribute("r:id");

                        System.out.println("oleObject r:id: " + oleObjectId);
                        System.out.println("objectPr r:id: " + objectPrId);
                    }
                }

                System.out.println(xlsx.getSheetAt(i).getRelationPartById(oleObjectId).getRelationship().getTargetURI());
                System.out.println(xlsx.getSheetAt(i).getRelationPartById(objectPrId).getRelationship().getTargetURI());
            }


            // XML 데이터 파싱
            //Document document = builder.parse(new InputSource(new StringReader(docx.getDocument().getBody().toString())));

            /*for (PackagePart pPart : xlsx.getAllEmbeddedParts()) {
                xOfficeEntryHandler.parser(pPart, fileDto.getOriginFileName(), fileDto.getFileOlePath());
            }*/

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(xlsx);
        }
    }
}
