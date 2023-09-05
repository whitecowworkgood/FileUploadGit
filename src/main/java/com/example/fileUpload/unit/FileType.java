package com.example.fileUpload.unit;

public enum FileType {

/*    PDF("pdf"),
    PNG("png"),
    JPG("jpg"),*/
    DOCX(".docx"),
    PPTX(".pptx"),
    XLSX(".xlsx"),
    DOC(".doc"),
    PPT(".ppt"),
    XLS(".xls"),
    BIN("bin"),
    HWP(".hwp"),
    POWERPOINT("PowerPoint"),
    EXCEL("Excel"),
    WORD("Word");

    private final String value;
    FileType(String value){
        this.value = value;

    }

    public String getValue(){
        return value;
    }

}
