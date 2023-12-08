package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.springframework.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;


public class ExcelExtractor extends OleExtractor {

    private final PackagePart packagePart;

    private FileOutputStream outputStream = null;
    private BufferedOutputStream bo = null;
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

        String uuid = addUniqueFileNameMapping(StringUtils.getFilename(String.valueOf(this.packagePart.getPartName())));

        super.stringBuffer.append(this.oleSavePath).append(uuid);

        this.outputStream = new FileOutputStream(super.stringBuffer.toString());
        this.bo = new BufferedOutputStream(this.outputStream);
        this.embeddedWorkbook.write(this.bo);
    }

    @Override
    protected void closeResources() {
        super.stringBuffer.delete(0, super.stringBuffer.length());
        IOUtils.closeQuietly(this.embeddedWorkbook);
        IOUtils.closeQuietly(this.outputStream);
        IOUtils.closeQuietly(this.bo);
    }

    public ExcelExtractor(PackagePart pPart, FileDto fileDto) {
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        //doExtract();
    }
}
