package com.example.fileUpload.unit;

public enum FileType {

    PDF("pdf"),
    PNG("png"),
    JPG("jpg"),
    DOCX("docx"),
    PPTX("pptx"),
    XLSX("xlsx"),
    BIN("bin"),
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
