package com.example.fileUpload.util.Enum;

public enum MimeType {
    PDF("application/pdf"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PPTX( "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PPT("application/vnd.ms-powerpoint"),
    XLS("application/vnd.ms-excel"),
    DOC("application/msword"),
    OLE_OBJECT("application/vnd.openxmlformats-officedocument.oleObject"),
    ZIP("application/zip"),
    HWP("application/octet-stream");

    private final String value;
    MimeType(String value){
        this.value = value;

    }

    public String getValue(){
        return value;
    }

}
