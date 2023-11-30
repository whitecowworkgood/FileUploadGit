package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.module.OleExtractor.OleExtractorFactory;
import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.xmlbeans.XmlException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

@NoArgsConstructor
public class XPowerPointParser extends OleExtractor {

    private FileInputStream fs = null;
    private BufferedInputStream bi = null;
    private XMLSlideShow pptx = null;

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws OpenXML4JException, IOException, XmlException {


        try{

            this.fs = new FileInputStream(fileDto.getFileTempPath());
            this.bi = new BufferedInputStream(this.fs);
            this.pptx = new XMLSlideShow(OPCPackage.open(this.bi));

            if(!this.pptx.getAllEmbeddedParts().isEmpty()){
                generateFolder(fileDto.getFileOlePath());

                for (PackagePart pPart : this.pptx.getAllEmbeddedParts())
                    new OleExtractorFactory().createModernOleExtractor(pPart, fileDto);
            }



        }catch (Exception e){
            catchException(e);

        }finally {
            closeResources();
        }
    }

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(this.fs);
        IOUtils.closeQuietly(this.pptx);
        IOUtils.closeQuietly(this.bi);
    }
}
