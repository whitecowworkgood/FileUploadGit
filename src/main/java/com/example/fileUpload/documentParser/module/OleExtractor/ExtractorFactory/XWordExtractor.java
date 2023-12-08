package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;


public class XWordExtractor extends OleExtractor {

    private final PackagePart packagePart;
    private FileOutputStream outputStream = null;
    private BufferedOutputStream bo = null;
    private OPCPackage docPackage = null;
    private XWPFDocument document = null;

    public void doExtract(){


        try {
            writeXDocument();

        }catch (Exception e){
            catchException(e);

        }finally{
            closeResources();
        }
    }

    protected void writeXDocument() throws Exception {
        this.docPackage = OPCPackage.open(this.packagePart.getInputStream());
        this.document = new XWPFDocument(this.docPackage);

        String uuid = addUniqueFileNameMapping(StringUtils.getFilename(String.valueOf(this.packagePart.getPartName())));

        super.stringBuffer.append(this.oleSavePath).append(uuid);

        this.outputStream = new FileOutputStream(super.stringBuffer.toString());
        this.bo = new BufferedOutputStream(this.outputStream);
        this.document.write(this.bo);
    }
    @Override
    protected void closeResources() {
        super.stringBuffer.delete(0, super.stringBuffer.length());
        IOUtils.closeQuietly(this.docPackage);
        IOUtils.closeQuietly(this.document);
        IOUtils.closeQuietly(this.outputStream);
        IOUtils.closeQuietly(this.bo);
    }

    public XWordExtractor(PackagePart pPart, FileDto fileDto) {
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        //doExtract();
    }
}
