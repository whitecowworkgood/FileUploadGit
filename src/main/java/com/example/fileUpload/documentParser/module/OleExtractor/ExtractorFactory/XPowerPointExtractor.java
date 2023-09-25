package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;

import java.io.FileOutputStream;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removePath;

public class XPowerPointExtractor extends OleExtractor {

    private final String oleSavePath;
    private final PackagePart packagePart;

    FileOutputStream outputStream = null;
    OPCPackage docPackage = null;
    XSLFSlideShow slideShow = null;

    private void doExtract(){


        try {
            writeXPowerPoint();

        }catch (Exception e) {
            catchException(e);

        } finally{
            closeResources();
        }
    }

    private void writeXPowerPoint() throws Exception {
        docPackage = OPCPackage.open(packagePart.getInputStream());
        slideShow = new XSLFSlideShow(docPackage);

        String uuid = addUniqueFileNameMapping(removePath(String.valueOf(packagePart.getPartName())));

        stringBuffer.append(oleSavePath).append(uuid);

        outputStream = new FileOutputStream(stringBuffer.toString());
        slideShow.write(outputStream);
    }
    @Override
    protected void closeResources() {
        stringBuffer.delete(0, stringBuffer.length());
        IOUtils.closeQuietly(docPackage);
        IOUtils.closeQuietly(slideShow);
        IOUtils.closeQuietly(outputStream);
    }

    public XPowerPointExtractor(PackagePart pPart, FileDto fileDto) {
        this.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        doExtract();
    }
}
