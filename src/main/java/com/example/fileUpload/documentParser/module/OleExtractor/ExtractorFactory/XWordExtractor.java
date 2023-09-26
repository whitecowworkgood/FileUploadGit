package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileOutputStream;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removePath;

public class XWordExtractor extends OleExtractor {

    //private final String oleSavePath;
    private final PackagePart packagePart;
    FileOutputStream outputStream = null;
    OPCPackage docPackage = null;
    XWPFDocument document = null;

    private void doExtract(){


        try {
            writeXDocuemnt();

        }catch (Exception e){
            catchException(e);

        }finally{
            closeResources();
        }
    }

    protected void writeXDocuemnt() throws Exception {
        docPackage = OPCPackage.open(packagePart.getInputStream());
        document = new XWPFDocument(docPackage);

        String uuid = addUniqueFileNameMapping(removePath(String.valueOf(packagePart.getPartName())));

        stringBuffer.append(oleSavePath).append(uuid);

        outputStream = new FileOutputStream(stringBuffer.toString());
        document.write(outputStream);
    }
    @Override
    protected void closeResources() {
        stringBuffer.delete(0, stringBuffer.length());
        IOUtils.closeQuietly(docPackage);
        IOUtils.closeQuietly(document);
        IOUtils.closeQuietly(outputStream);
    }

    public XWordExtractor(PackagePart pPart, FileDto fileDto) {
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        doExtract();
    }
}
