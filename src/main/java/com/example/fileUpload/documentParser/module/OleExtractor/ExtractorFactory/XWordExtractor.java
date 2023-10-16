package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileOutputStream;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removePath;

public class XWordExtractor extends OleExtractor {

    private final PackagePart packagePart;
    private FileOutputStream outputStream = null;
    private OPCPackage docPackage = null;
    private XWPFDocument document = null;

    public void doExtract(){


        try {
            writeXDocuemnt();

        }catch (Exception e){
            catchException(e);

        }finally{
            closeResources();
        }
    }

    protected void writeXDocuemnt() throws Exception {
        this.docPackage = OPCPackage.open(this.packagePart.getInputStream());
        this.document = new XWPFDocument(this.docPackage);

        String uuid = addUniqueFileNameMapping(removePath(String.valueOf(this.packagePart.getPartName())));

        super.stringBuffer.append(this.oleSavePath).append(uuid);

        this.outputStream = new FileOutputStream(super.stringBuffer.toString());
        this.document.write(this.outputStream);
    }
    @Override
    protected void closeResources() {
        super.stringBuffer.delete(0, super.stringBuffer.length());
        IOUtils.closeQuietly(this.docPackage);
        IOUtils.closeQuietly(this.document);
        IOUtils.closeQuietly(this.outputStream);
    }

    public XWordExtractor(PackagePart pPart, FileDto fileDto) {
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        //doExtract();
    }
}
