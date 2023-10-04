package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OleExtractor.OleExtractorFactory;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlException;

import java.io.FileInputStream;
import java.io.IOException;

import static com.example.fileUpload.documentParser.module.OleExtractor.OleExtractorFactory.choiceExtractor;

@NoArgsConstructor
public class XExcelParser extends OleExtractor {
    FileInputStream fs = null;
    XSSFWorkbook xlsx = null;

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException, OpenXML4JException {


        try{
            callOfficeHandler(fileDto);

<<<<<<< HEAD
        }catch (Exception e){
            catchException(e);

=======
            for (PackagePart pPart : xlsx.getAllEmbeddedParts()) {
                xOfficeEntryHandler.parser(pPart, fileDto.getOriginFileName(), fileDto.getFileOlePath());
            }

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
        } catch (XmlException e) {
            throw new RuntimeException(e);
>>>>>>> 0ab5e59af0fcc1dd27fd8a1fd530399b86c43cc9
        } finally {
            closeResources();
        }
    }

    @Override
    protected void callOfficeHandler(FileDto fileDto) throws Exception {
        fs = new FileInputStream(fileDto.getFileSavePath());
        xlsx = new XSSFWorkbook(OPCPackage.open(fs));

        for (PackagePart pPart : xlsx.getAllEmbeddedParts())
            new OleExtractorFactory().createMordernOleExtractor(pPart, fileDto);
            //choiceExtractor(pPart, fileDto);
            //


    }

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(fs);
        IOUtils.closeQuietly(xlsx);
    }
}
