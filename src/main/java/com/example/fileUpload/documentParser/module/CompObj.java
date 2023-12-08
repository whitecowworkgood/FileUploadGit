package com.example.fileUpload.documentParser.module;

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.StringUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author "WhiteCowWorkGood" - Github
 * CompObj를 따로 관리하는 클래스가 없어서 만들어 봄.
 * @version 1.0.0
 * */

public class CompObj {

    private static int MAX_RECORD_LENGTH = 100000000;
    private static int MAX_STRING_LENGTH = 1024;


    private int ansiUserTypeSize;
    private String ansiUserType;
    private int ansiClipboardFormatSize;
    private String ansiClipboardFormat;
    private int Reserved;
    private String PrefixedAnsiString;


    public CompObj(byte[] data, int offset) throws IOException {
        LittleEndianByteArrayInputStream leis = new LittleEndianByteArrayInputStream(data, offset);
        leis.skipNBytes(28);

        this.ansiUserTypeSize = leis.readInt();
        this.ansiUserType = readAsciiZ(leis);
        this.ansiClipboardFormatSize = leis.readInt();

        if (this.ansiClipboardFormatSize > 0) {
            this.ansiClipboardFormat = readAsciiZ(leis);
        }
        else{
            this.ansiClipboardFormat="";
        }

        this.Reserved = leis.readInt();
        this.PrefixedAnsiString = readAsciiZ(leis);


        IOUtils.closeQuietly(leis);
    }

    public String getFileExtraction(){
        if (PrefixedAnsiString.startsWith("Excel.Sheet.12") || ansiUserType.equals("Microsoft Excel Worksheet")) {
            return ".xlsx";
        }
        if (PrefixedAnsiString.startsWith("Word.Document.12") || ansiUserType.equals("Microsoft Word Document")) {
            return ".docx";
        }
        if (PrefixedAnsiString.startsWith("PowerPoint.Show.12") || ansiUserType.equals("Microsoft PowerPoint Presentation")) {
            return ".pptx";
        }
        if (PrefixedAnsiString.startsWith("PowerPoint.OpenDocumentPresentation.12")) {
            return ".odp";
        }
        if (PrefixedAnsiString.startsWith("Excel.OpenDocumentSpreadsheet.12")) {
            return ".ods";
        }
        if (PrefixedAnsiString.startsWith("Word.OpenDocumentText.12")) {
            return ".odt";
        }
        if(PrefixedAnsiString.startsWith("PBrush")){
            return ".bmp";
        }
        if(PrefixedAnsiString.startsWith("Excel.SheetMacroEnabled.12") || ansiUserType.startsWith("Microsoft Excel Macro-Enabled")){
            return ".csv";
        }
        return ".bin";
    }

    public static CompObj createFromEmbeddedCompObj(DirectoryNode directory) throws IOException {

        DocumentEntry nativeEntry = (DocumentEntry)directory.getEntry("\u0001CompObj");
        DocumentInputStream dis = directory.createDocumentInputStream(nativeEntry);
        Throwable var3 = null;

        CompObj var5;

        try {
            byte[] data = IOUtils.toByteArray(dis, nativeEntry.getSize(), MAX_RECORD_LENGTH);
            var5 = new CompObj(data, 0);
        } catch (Throwable var14) {
            var3 = var14;
            throw var14;
        } finally {
            if (dis != null) {
                if (var3 != null) {
                    try {
                        IOUtils.closeQuietly(dis);
                    } catch (Throwable var13) {
                        var3.addSuppressed(var13);
                    }
                } else {
                    IOUtils.closeQuietly(dis);
                }
            }

        }

        return var5;


    }

    private static String readAsciiZ(LittleEndianInput is){
        byte[] buf = new byte[MAX_STRING_LENGTH];

        for(int i = 0; i < buf.length; ++i) {
            if ((buf[i] = is.readByte()) == 0) {
                return new String(buf, 0, i, Charset.forName("euc-kr"));
            }
        }

        throw new RuntimeException("AsciiZ string was not null terminated after " + MAX_STRING_LENGTH + " bytes - Exiting.");
    }
    public int getAnsiUserTypeSize() {
        return ansiUserTypeSize;
    }

    public String getAnsiUserType() {
        return ansiUserType;
    }

    public int getAnsiClipboardFormatSize() {
        return ansiClipboardFormatSize;
    }

    public String getAnsiClipboardFormat() {
        return ansiClipboardFormat;
    }

    public int getReserved() {
        return Reserved;
    }

    public String getPrefixedAnsiString() {
        return PrefixedAnsiString;
    }
}
