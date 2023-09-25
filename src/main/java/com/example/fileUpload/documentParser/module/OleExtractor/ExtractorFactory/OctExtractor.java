package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.util.FileType;
import com.example.fileUpload.util.OleEntry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removeFileExtension;

public class OctExtractor extends OleExtractor {

    private final String  originalFileName;
    private final String oleSavePath;

    private final PackagePart packagePart;

    private void doExtract() throws Exception {
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(packagePart.getInputStream());
        FileOutputStream outputStream = null;

        DirectoryNode directoryNode = poifsFileSystem.getRoot();

        if(directoryNode.hasEntry(OleEntry.HWPINFO.getValue())){

            stringBuffer.append(removeFileExtension(originalFileName)).append("_OLE").append(FileType.HWP.getValue());
            String fileName = stringBuffer.toString();
            stringBuffer.delete(0, stringBuffer.length());

            String uuid = addUniqueFileNameMapping(fileName);

            stringBuffer.append(oleSavePath).append(File.separator).append(uuid);
            try {

                outputStream = new FileOutputStream(stringBuffer.toString());
                poifsFileSystem.writeFilesystem(outputStream);

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
            }finally {
                stringBuffer.delete(0, stringBuffer.length());
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(poifsFileSystem);

            }

        }else if(directoryNode.hasEntry(OleEntry.WORD.getValue())){

            stringBuffer.append(removeFileExtension(originalFileName)).append("_OLE").append(FileType.RTF.getValue());
            String fileName = stringBuffer.toString();
            stringBuffer.delete(0, stringBuffer.length());

            String uuid = addUniqueFileNameMapping(fileName);

            stringBuffer.append(oleSavePath).append(File.separator).append(uuid);
            try {

                outputStream = new FileOutputStream(stringBuffer.toString());
                poifsFileSystem.writeFilesystem(outputStream);

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
            }finally {
                stringBuffer.delete(0, stringBuffer.length());
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(poifsFileSystem);

            }

        }else if(directoryNode.hasEntry(Ole10Native.OLE10_NATIVE)){
            String bmpType =embeddedFileExtractor.parseFileType((DocumentEntry) poifsFileSystem.getRoot().getEntry(OleEntry.COMPOBJ.getValue()));

            if(bmpType!=null && bmpType.equals(FileType.BMP.getValue())){
                DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(Ole10Native.OLE10_NATIVE));
                oleStream.skipNBytes(4);

                stringBuffer.append(removeFileExtension(originalFileName)).append("_OLE").append(FileType.BMP.getValue());
                String uuid = addUniqueFileNameMapping(stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());

                stringBuffer.append(oleSavePath).append(uuid);

                try {
                    outputStream = new FileOutputStream(stringBuffer.toString());
                    outputStream.write(oleStream.readAllBytes());

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);

                } finally {
                    stringBuffer.delete(0, stringBuffer.length());
                    IOUtils.closeQuietly(oleStream);
                    IOUtils.closeQuietly(outputStream);
                }

            }else{
                DocumentEntry ole10Native = (DocumentEntry) directoryNode.getEntry(Ole10Native.OLE10_NATIVE);
                embeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), oleSavePath);
            }


        }else if (directoryNode.hasEntry(OleEntry.ODF.getValue())) {

            DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(OleEntry.ODF.getValue()));

            if(!directoryNode.hasEntry(OleEntry.COMPOBJ.getValue())){
                stringBuffer.append(removeFileExtension(originalFileName)).append("_OLE").append(FileType.ODT.getValue());
            }else{
                stringBuffer.append(removeFileExtension(originalFileName)).append("_OLE").append(embeddedFileExtractor.parseFileType((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue())));
            }

            String uuid = addUniqueFileNameMapping(stringBuffer.toString());
            stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append(oleSavePath).append(uuid);

            try {
                outputStream = new FileOutputStream(stringBuffer.toString());
                outputStream.write(oleStream.readAllBytes());

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
            } finally {
                stringBuffer.delete(0, stringBuffer.length());
                IOUtils.closeQuietly(oleStream);
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

    @Override
    protected void closeResources() {

    }

    public OctExtractor(PackagePart pPart, FileDto fileDto) throws Exception {
        this.originalFileName = fileDto.getOriginFileName();
        this.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;
        doExtract();
    }
}
