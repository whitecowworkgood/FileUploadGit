package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.PackagePart;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removePath;

public class ExcelExtractor extends OleExtractor {

    private final PackagePart packagePart;

    private FileOutputStream outputStream = null;
    private HSSFWorkbook embeddedWorkbook = null;

    public void doExtract() {

        try {
            writeExcelFile();

        }catch (IOException e){
            catchIOException(e);

        }finally{
            closeResources();

        }
    }

    private void writeExcelFile() throws IOException {
        this.embeddedWorkbook = new HSSFWorkbook(this.packagePart.getInputStream());
        this.embeddedWorkbook.setHidden(false);

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
    }

    public ExcelExtractor(PackagePart pPart, FileDto fileDto) {
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        //doExtract();
    }
}
