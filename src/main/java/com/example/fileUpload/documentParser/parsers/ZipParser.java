package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.Tika;

import java.io.FileInputStream;
import java.io.IOException;

public class ZipParser extends OleExtractor {
    FileInputStream fs = null;
    ZipArchiveInputStream zais = null;
    ZipArchiveEntry entry = null;
    Tika tika = new Tika();

    @Override
    public void extractOleFromDocumentFile(FileDto fileDto) throws Exception {

        try {
            showZipDocument(fileDto);

        } catch (IOException e) {
            catchIOException(e);

        } finally {
            closeResources();
        }
    }
    private void showZipDocument(FileDto fileDto) throws IOException {
        fs = new FileInputStream(fileDto.getFileSavePath());
        zais = new ZipArchiveInputStream(fs, "EUC-KR", true);
        while((entry = zais.getNextZipEntry()) != null) {
            System.out.println(tika.detect(entry.getName()));
            System.out.print(entry.getTime() + "  ");
            System.out.print(entry.getName() + "  ");
            System.out.print(entry.getComment() + "  ");
            System.out.print(entry.getCompressedSize() + "  ");
            System.out.print(entry.getCrc() + "  ");
            System.out.print(entry.getPlatform() + "  ");
            System.out.print(entry.getUnparseableExtraFieldData());
            System.out.println();

        }
    }

    @Override
    protected void closeResources() {
        IOUtils.closeQuietly(fs);
        IOUtils.closeQuietly(zais);
    }
}
