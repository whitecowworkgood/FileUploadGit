package com.example.fileUpload.unit;

public enum MimeType {
    PDF("application/pdf"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PPTX( "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PPT("application/vnd.ms-powerpoint"),
    XLS("application/vnd.ms-excel"),
    DOC("application/msword");

    private final String value;
    MimeType(String value){
        this.value = value;

    }

    public String getValue(){
        return value;
    }

}
