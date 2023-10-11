package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.parsers.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

import java.io.FileInputStream;
import java.io.IOException;

public class ZipParser extends OleExtractor {
    private FileInputStream fs = null;
    private ZipArchiveInputStream zais = null;
    private ZipArchiveEntry entry = null;
    private final Tika tika = new Tika();

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
        this.fs = new FileInputStream(fileDto.getFileSavePath());
        this.zais = new ZipArchiveInputStream(this.fs, "EUC-KR", true);
        while((this.entry = this.zais.getNextZipEntry()) != null) {

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
        IOUtils.closeQuietly(this.fs);
        IOUtils.closeQuietly(this.zais);
    }
}
