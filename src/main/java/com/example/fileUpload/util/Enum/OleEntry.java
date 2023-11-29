package com.example.fileUpload.util.Enum;

public enum OleEntry {
    OBJECTPOOL("ObjectPool"),
    PACKAGE("Package"),
    COMPOBJ("\u0001CompObj"),
    HWPINFO("\u0005HwpSummaryInformation"),
    ODF("EmbeddedOdf"),
    WORD("WordDocument"),
    PPT("PowerPoint Document"),
    XLS("Workbook");

    private final String value;
    OleEntry(String value){
        this.value = value;

    }

    public String getValue(){
        return this.value;
    }
}
