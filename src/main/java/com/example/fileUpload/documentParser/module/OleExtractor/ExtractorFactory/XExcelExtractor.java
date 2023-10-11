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

    private final PackagePart packagePart;
    private FileOutputStream outputStream = null;
    private OPCPackage docPackage = null;
    private XSSFWorkbook embeddedWorkbook = null;

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
        this.docPackage = OPCPackage.open(this.packagePart.getInputStream());
        this.embeddedWorkbook = new XSSFWorkbook(this.docPackage);

        CTBookView[] cb = this.embeddedWorkbook.getCTWorkbook().getBookViews().getWorkbookViewArray();

        cb[0].setVisibility(STVisibility.VISIBLE);
        this.embeddedWorkbook.getCTWorkbook().getBookViews().setWorkbookViewArray(cb);

        String uuid = addUniqueFileNameMapping(removePath(String.valueOf(this.packagePart.getPartName())));

        super.stringBuffer.append(this.oleSavePath).append(uuid);
        this.outputStream = new FileOutputStream(super.stringBuffer.toString());
        this.embeddedWorkbook.write(this.outputStream);
    }
    @Override
    protected void closeResources() {
        super.stringBuffer.delete(0, super.stringBuffer.length());
        IOUtils.closeQuietly(this.embeddedWorkbook);
        IOUtils.closeQuietly(this.outputStream);
        IOUtils.closeQuietly(this.docPackage);
    }

    public XExcelExtractor(PackagePart pPart, FileDto fileDto) {

        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        doExtract();
    }

}
