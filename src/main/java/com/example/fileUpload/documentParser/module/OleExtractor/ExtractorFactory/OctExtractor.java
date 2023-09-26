package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.util.FileType;
import com.example.fileUpload.util.OleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removeFileExtension;


public class OctExtractor extends OleExtractor {

    //private final String  originalFileName;
    //private final String oleSavePath;
    private final PackagePart packagePart;

    private POIFSFileSystem poifsFileSystem =null;
    private DirectoryNode directoryNode = null;
    private DocumentInputStream oleStream = null;
    //private String fileName = null;

    private void doExtract() throws Exception {
        poifsFileSystem = new POIFSFileSystem(packagePart.getInputStream());
        directoryNode = poifsFileSystem.getRoot();

        if(directoryNode.hasEntry(OleEntry.HWPINFO.getValue())){
            fileName = buildPathFileName(FileType.HWP.getValue());

            tryFileSaveUsePOIFStream();

        }
        if(directoryNode.hasEntry(Ole10Native.OLE10_NATIVE)){

            String type =embeddedFileExtractor.parseFileType((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue()));

            if(type.equals(FileType.BMP.getValue())){

                fileName = buildPathFileName(FileType.BMP.getValue());

                oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(Ole10Native.OLE10_NATIVE));
                oleStream.skipNBytes(4);

                tryFileSaveUseOleStream();
            }
            if(type.equals("other")){

                DocumentEntry ole10Native = (DocumentEntry) directoryNode.getEntry(Ole10Native.OLE10_NATIVE);
                embeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), oleSavePath);
            }
        }
        if (directoryNode.hasEntry(OleEntry.ODF.getValue())) {

            oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(OleEntry.ODF.getValue()));
            fileName = selectODFType();

            tryFileSaveUseOleStream();

        }

    }

    private void tryFileSaveUseOleStream(){
        try {
            new FileOutputStream(buildOutputPath()).write(oleStream.readAllBytes());

        } catch (IOException e) {
            catchIOException(e);

        } finally {
            closeResources();
        }
    }

    private void tryFileSaveUsePOIFStream(){
        try {
            poifsFileSystem.writeFilesystem(new FileOutputStream(buildOutputPath()));

        }catch (IOException e) {
            catchIOException(e);

        }finally {
            closeResources();

        }
    }
    private String selectODFType() throws FileNotFoundException {
        if(directoryNode.hasEntry(OleEntry.COMPOBJ.getValue())){
            return buildPathFileName((embeddedFileExtractor.parseFileType((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue()))));

        }
        return buildPathFileName(FileType.ODT.getValue());
    }
    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(poifsFileSystem);
        IOUtils.closeQuietly(oleStream);
    }

    public OctExtractor(PackagePart pPart, FileDto fileDto) throws Exception {
        super.originalFileName = fileDto.getOriginFileName();
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        doExtract();
    }
}
