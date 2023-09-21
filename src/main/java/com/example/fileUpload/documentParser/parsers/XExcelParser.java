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
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.EMFParser;
import org.apache.tika.sax.BodyContentHandler;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class XExcelParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws IOException, OpenXML4JException {

/*
        String oleObjectId = null;
        String objectPrId = null;
*/

        FileInputStream fs = null;
        XSSFWorkbook xlsx = null;
        XOfficeEntryHandler xOfficeEntryHandler = new XOfficeEntryHandler();
        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            xlsx = new XSSFWorkbook(OPCPackage.open(fs));


            /*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            //System.out.println(xlsx.getSheetAt(3).getCTWorksheet().getOleObjects().toString());

            for (int i = 0; i < xlsx.getNumberOfSheets(); i++) {
                // System.out.println(xlsx.getSheetAt(i).getCTWorksheet().toString());

                InputSource inputSource = new InputSource(new StringReader(xlsx.getSheetAt(i).getCTWorksheet().toString()));
                Document document = builder.parse(inputSource);

                NodeList oleObjectNodes = document.getElementsByTagName("main:oleObject");

                for (int j = 0; j < oleObjectNodes.getLength(); j++) {
                    Element oleObjectElement = (Element) oleObjectNodes.item(j);
                    oleObjectId = oleObjectElement.getAttribute("r:id");

                    NodeList objectPrNodes = oleObjectElement.getElementsByTagName("main:objectPr");
                    for (int k = 0; k < objectPrNodes.getLength(); k++) {
                        Element objectPrElement = (Element) objectPrNodes.item(k);
                        objectPrId = objectPrElement.getAttribute("r:id");

                        System.out.println("oleObject r:id: " + oleObjectId);
                        System.out.println("objectPr r:id: " + objectPrId);

                        System.out.println(xlsx.getSheetAt(i).getRelationPartById(oleObjectId).getDocumentPart().getPackagePart());
                        System.out.println(xlsx.getSheetAt(i).getRelationPartById(objectPrId).getDocumentPart().getPackagePart().getPartName().getExtension());
                        System.out.println();

                        if(xlsx.getSheetAt(i).getRelationPartById(objectPrId).getDocumentPart().getPackagePart().getPartName().getExtension().equals("emf")){
                            EMFParser parser = new EMFParser();
                            Metadata metadata = new Metadata();
                            ParseContext context = new ParseContext();

                            BodyContentHandler handler = new BodyContentHandler();

                            // EMF 파일 파싱
                            parser.parse(xlsx.getSheetAt(i).getRelationPartById(objectPrId).getDocumentPart().getPackagePart().getInputStream(), handler, metadata, context);

                            // 추출된 텍스트 출력
                            String extractedText = handler.toString();

                            String concatenated = extractedText.replace("\n", "");
                            // 정규표현식 패턴 설정
                            Pattern pattern = Pattern.compile("^(.*?)\\.([^.]+)$");

                            // 정규표현식과 매칭
                            Matcher matcher = pattern.matcher(extractedText);

                            if (!matcher.matches()) {
                                concatenated="";
                            }
                            System.out.println(concatenated);

                        }

                        System.out.println(xlsx.getSheetAt(i).getRelationPartById(oleObjectId).getRelationship().getSource());
                        System.out.println(xlsx.getSheetAt(i).getRelationPartById(objectPrId).getRelationship().getSource());
                    }
                }

                System.out.println(xlsx.getSheetAt(i).getRelationPartById(oleObjectId).getRelationship().getTargetURI());
                System.out.println(xlsx.getSheetAt(i).getRelationPartById(objectPrId).getRelationship().getTargetURI());
            }*/

            // XML 데이터 파싱
            //Document document = builder.parse(new InputSource(new StringReader(docx.getDocument().getBody().toString())));

            for (PackagePart pPart : xlsx.getAllEmbeddedParts()) {
                xOfficeEntryHandler.parser(pPart, fileDto.getOriginFileName(), fileDto.getFileOlePath());
            }

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        } catch (XmlException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(xlsx);
        }
    }
}
