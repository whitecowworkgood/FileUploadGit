package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.openxml4j.opc.PackagePart;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removePath;

public class PowerPointExtractor extends OleExtractor {

    //private final String oleSavePath;
    private final PackagePart packagePart;
    FileOutputStream outputStream = null;
    HSLFSlideShow slideShow = null;

    private void doExtract() {

        try {
            writePowerPoint();

        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);

        }finally{
            closeResources();
        }
    }

    private void writePowerPoint() throws IOException {
        slideShow = new HSLFSlideShow(packagePart.getInputStream());

        String uuid = addUniqueFileNameMapping(removePath(String.valueOf(packagePart.getPartName())));

        stringBuffer.append(oleSavePath).append(uuid);

        outputStream = new FileOutputStream(stringBuffer.toString());
        slideShow.write(outputStream);
    }

    @Override
    protected void closeResources() {
        stringBuffer.delete(0, stringBuffer.length());
        IOUtils.closeQuietly(slideShow);
        IOUtils.closeQuietly(outputStream);
    }

    public PowerPointExtractor(PackagePart pPart, FileDto fileDto) {
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        doExtract();
    }
}
