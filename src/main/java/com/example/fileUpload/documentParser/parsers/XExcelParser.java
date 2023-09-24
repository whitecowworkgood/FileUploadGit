package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.XOfficeEntryHandler;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlException;

import java.io.FileInputStream;
import java.io.IOException;

@NoArgsConstructor
public class XExcelParser extends OleExtractor {
    FileInputStream fs = null;
    XSSFWorkbook xlsx = null;
    XOfficeEntryHandler xOfficeEntryHandler = new XOfficeEntryHandler();

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException, OpenXML4JException {


        try{
            fs = new FileInputStream(fileDto.getFileSavePath());
            xlsx = new XSSFWorkbook(OPCPackage.open(fs));

            for (PackagePart pPart : xlsx.getAllEmbeddedParts()) {
                xOfficeEntryHandler.parser(pPart, fileDto.getOriginFileName(), fileDto.getFileOlePath());
            }

        }catch (IOException e){
            catchIOException(e);

        } catch (XmlException e) {
            catchXmlException(e);

        } finally {
            closeResources();
        }
    }

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(fs);
        IOUtils.closeQuietly(xlsx);
    }
}
