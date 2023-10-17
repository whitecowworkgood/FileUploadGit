package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.util.Enum.FileType;
import com.example.fileUpload.util.OleEntry;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class OctExtractor extends OleExtractor {

    private final PackagePart packagePart;
    private POIFSFileSystem poifsFileSystem =null;
    private DirectoryNode directoryNode = null;
    private DocumentInputStream oleStream = null;

    private FileOutputStream fs = null;

    public void doExtract() throws Exception {
        this.poifsFileSystem = new POIFSFileSystem(this.packagePart.getInputStream());
        this.directoryNode = this.poifsFileSystem.getRoot();

        if(this.directoryNode.hasEntry(OleEntry.HWPINFO.getValue())){
            this.fileName = buildPathFileName(FileType.HWP.getValue());

            tryFileSaveUsePOIFStream();

        }
        if(this.directoryNode.hasEntry(Ole10Native.OLE10_NATIVE)){

            String type =super.embeddedFileExtractor.parseFileType((DocumentEntry) this.directoryNode.getEntry(OleEntry.COMPOBJ.getValue()));

            if(type.equals(FileType.BMP.getValue())){

                this.fileName = buildPathFileName(FileType.BMP.getValue());

                this.oleStream = new DocumentInputStream((DocumentEntry) this.directoryNode.getEntry(Ole10Native.OLE10_NATIVE));
                this.oleStream.skipNBytes(4);

                tryFileSaveUseOleStream();
            }
            if(type.equals("other")){

                DocumentEntry ole10Native = (DocumentEntry) this.directoryNode.getEntry(Ole10Native.OLE10_NATIVE);
                super.embeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), this.oleSavePath);
            }
        }
        if (this.directoryNode.hasEntry(OleEntry.ODF.getValue())) {

            this.oleStream = new DocumentInputStream((DocumentEntry) this.directoryNode.getEntry(OleEntry.ODF.getValue()));
            this.fileName = selectODFType();

            tryFileSaveUseOleStream();

        }

    }

    private void tryFileSaveUseOleStream(){
        try {
            this.fs = new FileOutputStream(buildOutputPath());
            this.fs.write(this.oleStream.readAllBytes());

        } catch (IOException e) {
            catchIOException(e);

        } finally {
            closeResources();
        }
    }

    private void tryFileSaveUsePOIFStream(){
        try {
            this.fs = new FileOutputStream(buildOutputPath());
            this.poifsFileSystem.writeFilesystem(this.fs);

        }catch (IOException e) {
            catchIOException(e);

        }finally {
            closeResources();

        }
    }
    private String selectODFType() throws FileNotFoundException {
        if(this.directoryNode.hasEntry(OleEntry.COMPOBJ.getValue())){
            return buildPathFileName((super.embeddedFileExtractor.parseFileType((DocumentEntry) this.directoryNode.getEntry(OleEntry.COMPOBJ.getValue()))));

        }
        return buildPathFileName(FileType.ODT.getValue());
    }
    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(this.poifsFileSystem);
        IOUtils.closeQuietly(this.oleStream);
        IOUtils.closeQuietly(this.fs);
    }

    public OctExtractor(PackagePart pPart, FileDto fileDto) throws Exception {
        super.originalFileName = fileDto.getOriginFileName();
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        //doExtract();
    }
}
