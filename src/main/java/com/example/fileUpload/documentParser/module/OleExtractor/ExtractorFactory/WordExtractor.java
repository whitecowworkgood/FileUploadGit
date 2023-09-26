package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.openxml4j.opc.PackagePart;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removePath;

public class WordExtractor extends OleExtractor {

    //private final String oleSavePath;
    private final PackagePart packagePart;

    FileOutputStream outputStream = null;
    HWPFDocument document = null;

    private void doExtract(){

        try {
            writeDocument();

        }catch (IOException e){
            catchIOException(e);

        }finally{
            closeResources();

        }
    }

    protected void writeDocument() throws IOException {
        document = new HWPFDocument(packagePart.getInputStream());
        String uuid = addUniqueFileNameMapping(removePath(String.valueOf(packagePart.getPartName())));

        stringBuffer.append(oleSavePath).append(uuid);

        outputStream = new FileOutputStream(stringBuffer.toString());
        document.write(outputStream);
    }

    @Override
    protected void closeResources() {
        stringBuffer.delete(0, stringBuffer.length());
        IOUtils.closeQuietly(document);
        IOUtils.closeQuietly(outputStream);
    }

    public WordExtractor(PackagePart pPart, FileDto fileDto) {
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        doExtract();
    }
}
