package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.openxml4j.opc.PackagePart;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removePath;

public class PowerPointExtractor extends OleExtractor {

    private final PackagePart packagePart;
    private FileOutputStream outputStream = null;
    private BufferedOutputStream bo = null;
    private HSLFSlideShow slideShow = null;

    public void doExtract() {

        try {
            writePowerPoint();

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);

        }finally{
            closeResources();
        }
    }

    private void writePowerPoint() throws IOException {
        this.slideShow = new HSLFSlideShow(this.packagePart.getInputStream());

        String uuid = addUniqueFileNameMapping(removePath(String.valueOf(this.packagePart.getPartName())));

        this.stringBuffer.append(this.oleSavePath).append(uuid);

        this.outputStream = new FileOutputStream(this.stringBuffer.toString());
        this.bo = new BufferedOutputStream(this.outputStream);
        this.slideShow.write(this.bo);
    }

    @Override
    protected void closeResources() {
        this.stringBuffer.delete(0, this.stringBuffer.length());
        IOUtils.closeQuietly(this.slideShow);
        IOUtils.closeQuietly(this.outputStream);
        IOUtils.closeQuietly(this.bo);
    }

    public PowerPointExtractor(PackagePart pPart, FileDto fileDto) {
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        //doExtract();
    }
}
