package com.example.fileUpload.documentParser.parsers;

import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import com.example.fileUpload.model.FileDto;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.Tika;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipParser extends FileParser {

    @Override
    public void parse(FileDto fileDto) throws Exception {

        FileInputStream fs = null;
        ZipArchiveInputStream zais = null;
        ZipArchiveEntry entry = null;
        Tika tika = new Tika();

        try {
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

        } catch (Exception e) {
            ExceptionUtils.getStackTrace(e);
        } finally {
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(zais);
        }
    }

}
