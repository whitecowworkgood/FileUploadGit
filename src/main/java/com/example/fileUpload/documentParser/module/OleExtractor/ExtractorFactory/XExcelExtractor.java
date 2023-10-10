package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;

import java.io.FileOutputStream;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removePath;

public class XExcelExtractor extends OleExtractor {

    //private final String oleSavePath;
    private final PackagePart packagePart;
    FileOutputStream outputStream = null;
    OPCPackage docPackage = null;
    XSSFWorkbook embeddedWorkbook = null;

    private void doExtract(){


        try {
            writeXExcel();

        }catch (Exception e){
            catchException(e);

        } finally{
            closeResources();
        }
    }

    protected void writeXExcel() throws Exception {
        docPackage = OPCPackage.open(packagePart.getInputStream());
        embeddedWorkbook = new XSSFWorkbook(docPackage);

        CTBookView[] cb = embeddedWorkbook.getCTWorkbook().getBookViews().getWorkbookViewArray();

        cb[0].setVisibility(STVisibility.VISIBLE);
        embeddedWorkbook.getCTWorkbook().getBookViews().setWorkbookViewArray(cb);

        String uuid = addUniqueFileNameMapping(removePath(String.valueOf(packagePart.getPartName())));

        stringBuffer.append(oleSavePath).append(uuid);
        outputStream = new FileOutputStream(stringBuffer.toString());
        embeddedWorkbook.write(outputStream);
    }
    @Override
    protected void closeResources() {
        stringBuffer.delete(0, stringBuffer.length());
        IOUtils.closeQuietly(embeddedWorkbook);
        IOUtils.closeQuietly(outputStream);
        IOUtils.closeQuietly(docPackage);
    }

    public XExcelExtractor(PackagePart pPart, FileDto fileDto) {

        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        doExtract();
    }

}
