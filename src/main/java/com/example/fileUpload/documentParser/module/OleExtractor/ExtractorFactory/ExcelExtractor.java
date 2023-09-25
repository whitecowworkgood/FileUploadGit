package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.PackagePart;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removePath;

public class ExcelExtractor extends OleExtractor {

    private final String oleSavePath;
    private final PackagePart packagePart;

    FileOutputStream outputStream = null;
    HSSFWorkbook embeddedWorkbook = null;

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
        embeddedWorkbook = new HSSFWorkbook(packagePart.getInputStream());
        embeddedWorkbook.setHidden(false);

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
    }

    public ExcelExtractor(PackagePart pPart, FileDto fileDto) {
        this.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        doExtract();
    }
}
