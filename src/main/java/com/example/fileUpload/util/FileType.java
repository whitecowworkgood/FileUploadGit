package com.example.fileUpload.util;

public enum FileType {

    DOCX(".docx"),
    PPTX(".pptx"),
    XLSX(".xlsx"),
    CSV(".csv"),
    DOC(".doc"),
    PPT(".ppt"),
    XLS(".xls"),
    BIN("bin"),
    HWP(".hwp"),
    POWERPOINT("PowerPoint"),
    EXCEL("Excel"),
    WORD("Word"),
    BMP(".bmp"),
    RTF(".rtf"),
    ODP(".odp"),
    ODT(".odt"),
    ODS(".ods");

    private final String value;
    FileType(String value){
        this.value = value;

    }

    public String getValue(){
        return value;
    }

}
