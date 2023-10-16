package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OleExtractor.OleExtractorFactory;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

@NoArgsConstructor
public class XExcelParser extends OleExtractor {
    private FileInputStream fs = null;
    private XSSFWorkbook xlsx = null;

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws IOException, OpenXML4JException {


        try{
           //callOfficeHandler(fileDto);
            this.fs = new FileInputStream(fileDto.getFileSavePath());
            this.xlsx = new XSSFWorkbook(OPCPackage.open(this.fs));

            for (PackagePart pPart : this.xlsx.getAllEmbeddedParts())
                new OleExtractorFactory().createMordernOleExtractor(pPart, fileDto);

        }catch (Exception e){
            catchException(e);
            
        } finally {
            closeResources();
        }
    }

    //@Override
/*    protected void callOfficeHandler(FileDto fileDto) throws Exception {


    }*/

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(this.fs);
        IOUtils.closeQuietly(this.xlsx);
    }
}
