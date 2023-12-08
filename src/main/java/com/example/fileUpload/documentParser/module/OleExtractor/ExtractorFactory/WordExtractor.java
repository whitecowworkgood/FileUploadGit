package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.util.StringUtils;

import java.io.*;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;


public class WordExtractor extends OleExtractor {

    private final PackagePart packagePart;

    private FileOutputStream outputStream = null;
    private BufferedOutputStream bo = null;
    private HWPFDocument document = null;

    public void doExtract(){

        try {
            writeDocument();

        }catch (IOException e){
            catchIOException(e);

        }finally{
            closeResources();

        }
    }

    protected void writeDocument() throws IOException {
        this.document = new HWPFDocument(this.packagePart.getInputStream());
        String uuid = addUniqueFileNameMapping(StringUtils.getFilename(String.valueOf(this.packagePart.getPartName())));

        super.stringBuffer.append(this.oleSavePath).append(uuid);

        this.outputStream = new FileOutputStream(super.stringBuffer.toString());
        this.bo = new BufferedOutputStream(this.outputStream);
        this.document.write(this.bo);
    }

    @Override
    protected void closeResources() {
        super.stringBuffer.delete(0, super.stringBuffer.length());
        IOUtils.closeQuietly(this.document);
        IOUtils.closeQuietly(this.outputStream);
        IOUtils.closeQuietly(this.bo);
    }

    public WordExtractor(PackagePart pPart, FileDto fileDto) {
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        //doExtract();
    }
}
