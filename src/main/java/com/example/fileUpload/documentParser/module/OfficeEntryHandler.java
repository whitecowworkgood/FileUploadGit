package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.util.FileType;
import com.example.fileUpload.util.OleEntry;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.ss.extractor.EmbeddedData;
import org.apache.poi.ss.extractor.EmbeddedExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;
import static com.example.fileUpload.util.FileUtil.removeFileExtension;

@Slf4j
@NoArgsConstructor
public class OfficeEntryHandler {
    static StringBuilder stringBuilder = new StringBuilder();
    public void parser(DirectoryNode directoryNode, String OriginalFileName, String OLESavePath) throws IOException {

        EmbeddedFileExtractor embeddedFileExtractor = new EmbeddedFileExtractor();

        FileOutputStream outputStream = null;

        if(directoryNode.hasEntry(Ole10Native.OLE10_NATIVE)){
            String bmpType = embeddedFileExtractor.parseFileType((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue()));

            if(bmpType!=null && bmpType.equals(FileType.BMP.getValue())){
                DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(Ole10Native.OLE10_NATIVE));
                oleStream.skipNBytes(4);

                stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.BMP.getValue());
                String uuid = addUniqueFileNameMapping(stringBuilder.toString());
                stringBuilder.setLength(0);

                stringBuilder.append(OLESavePath).append(uuid);

                try {
                    outputStream = new FileOutputStream(stringBuilder.toString());
                    outputStream.write(oleStream.readAllBytes());

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                } finally {
                    stringBuilder.setLength(0);
                    IOUtils.closeQuietly(oleStream);
                    IOUtils.closeQuietly(outputStream);
                }

            }else{
                DocumentEntry ole10Native = (DocumentEntry) directoryNode.getEntry(Ole10Native.OLE10_NATIVE);
                embeddedFileExtractor.parseOle10NativeEntry(new DocumentInputStream(ole10Native), OLESavePath);
            }


        }else if(directoryNode.hasEntry(OleEntry.XLS.getValue())){

                if (directoryNode.hasEntry("\u0001Ole")) {
                    directoryNode.getEntry("\u0001Ole").delete();
                }

                EmbeddedExtractor extractor = new EmbeddedExtractor();
                EmbeddedData embeddedData = extractor.extractOne(directoryNode);

                HSSFWorkbook hs = new HSSFWorkbook(new ByteArrayInputStream(embeddedData.getEmbeddedData()));
                hs.setHidden(false);

                stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.XLS.getValue());
                String uuid = addUniqueFileNameMapping(stringBuilder.toString());
                stringBuilder.setLength(0);

                stringBuilder.append(OLESavePath).append(uuid);

                try {
                    outputStream = new FileOutputStream(stringBuilder.toString());
                    hs.write(outputStream);

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                } finally {
                    stringBuilder.setLength(0);
                    IOUtils.closeQuietly(outputStream);
                    IOUtils.closeQuietly(hs);
                }

        }else if(directoryNode.hasEntry(OleEntry.PPT.getValue())){
            if (directoryNode.hasEntry("\u0001Ole")) {
                directoryNode.getEntry("\u0001Ole").delete();
            }
            EmbeddedExtractor extractor = new EmbeddedExtractor();
            EmbeddedData embeddedData = extractor.extractOne(directoryNode);

            stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.PPT.getValue());
            String uuid = addUniqueFileNameMapping(stringBuilder.toString());
            stringBuilder.setLength(0);

            stringBuilder.append(OLESavePath).append(uuid);

            try {
                outputStream = new FileOutputStream(stringBuilder.toString());
                outputStream.write(embeddedData.getEmbeddedData());

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            } finally {
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(outputStream);
            }

        }else if(directoryNode.hasEntry(OleEntry.WORD.getValue())){
            EmbeddedExtractor extractor = new EmbeddedExtractor();


            if (directoryNode.hasEntry("\u0001Ole")) {
                directoryNode.getEntry("\u0001Ole").delete();
            }

            EmbeddedData embeddedData = extractor.extractOne(directoryNode);

            stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.DOC.getValue());
            String uuid = addUniqueFileNameMapping(stringBuilder.toString());
            stringBuilder.setLength(0);

            stringBuilder.append(OLESavePath).append(uuid);

            try {
                outputStream = new FileOutputStream(stringBuilder.toString());
                outputStream.write(embeddedData.getEmbeddedData());

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            } finally {
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(outputStream);
            }
        }else if (directoryNode.hasEntry(OleEntry.HWPINFO.getValue())) {

            if (directoryNode.hasEntry("\u0001Ole")) {
                directoryNode.getEntry("\u0001Ole").delete();
            }
            EmbeddedExtractor extractor = new EmbeddedExtractor();

            EmbeddedData embeddedData = extractor.extractOne(directoryNode);

            stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.HWP.getValue());
            String uuid = addUniqueFileNameMapping(stringBuilder.toString());
            stringBuilder.setLength(0);

            stringBuilder.append(OLESavePath).append(uuid);

            try {
                outputStream = new FileOutputStream(stringBuilder.toString());
                outputStream.write(embeddedData.getEmbeddedData());

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            } finally {
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(outputStream);
            }

        } else if (directoryNode.hasEntry(OleEntry.ODF.getValue())) {

            DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(OleEntry.ODF.getValue()));

            if(!directoryNode.hasEntry(OleEntry.COMPOBJ.getValue())){
                stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.ODT.getValue());
            }else{
                stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(embeddedFileExtractor.parseFileType((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue())));
            }

            String uuid = addUniqueFileNameMapping(stringBuilder.toString());
            stringBuilder.setLength(0);

            stringBuilder.append(OLESavePath).append(uuid);

            try {
                outputStream = new FileOutputStream(stringBuilder.toString());
                outputStream.write(oleStream.readAllBytes());

            } catch (IOException e) {
                ExceptionUtils.getStackTrace(e);
                log.error("파일 저장 실패");
            } finally {
                stringBuilder.setLength(0);
                IOUtils.closeQuietly(oleStream);
                IOUtils.closeQuietly(outputStream);
            }

        } else if (directoryNode.hasEntry(OleEntry.PACKAGE.getValue())) {
            DocumentInputStream oleStream = new DocumentInputStream((DocumentEntry) directoryNode.getEntry(OleEntry.PACKAGE.getValue()));

            String type = embeddedFileExtractor.parseFileType((DocumentEntry) directoryNode.getEntry(OleEntry.COMPOBJ.getValue()));

            if(type!=null && type.equals(FileType.XLSX.getValue())){
                XSSFWorkbook xs = new XSSFWorkbook(oleStream);

                CTBookView[] cb = xs.getCTWorkbook().getBookViews().getWorkbookViewArray();

                cb[0].setVisibility(STVisibility.VISIBLE);
                xs.getCTWorkbook().getBookViews().setWorkbookViewArray(cb);

                stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(FileType.XLSX.getValue());
                String uuid = addUniqueFileNameMapping(stringBuilder.toString());
                stringBuilder.setLength(0);

                stringBuilder.append(OLESavePath).append(uuid);

                try {
                    outputStream = new FileOutputStream(stringBuilder.toString());
                    xs.write(outputStream);

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                } finally {
                    stringBuilder.setLength(0);
                    IOUtils.closeQuietly(oleStream);
                    IOUtils.closeQuietly(outputStream);
                    IOUtils.closeQuietly(xs);
                }

            }else{
                stringBuilder.append(removeFileExtension(OriginalFileName)).append("_OLE").append(type);

                String uuid = addUniqueFileNameMapping(stringBuilder.toString());
                stringBuilder.setLength(0);

                stringBuilder.append(OLESavePath).append(uuid);

                try {
                    outputStream = new FileOutputStream(stringBuilder.toString());
                    outputStream.write(oleStream.readAllBytes());

                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                } finally {
                    stringBuilder.setLength(0);
                    IOUtils.closeQuietly(oleStream);
                    IOUtils.closeQuietly(outputStream);
                }
            }

        }

    }

}
