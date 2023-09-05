package com.example.fileUpload.unit;

public enum OleEntry {
    OBJECTPOOL("ObjectPool"),
    PACKAGE("Package"),
    COMPOBJ("\u0001CompObj"),
    HWPINFO("\u0005HwpSummaryInformation");


    private final String value;
    OleEntry(String value){
        this.value = value;

    }

    public String getValue(){
        return value;
    }
}
