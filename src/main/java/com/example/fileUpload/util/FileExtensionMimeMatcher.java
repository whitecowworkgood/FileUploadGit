package com.example.fileUpload.util;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

import java.util.HashMap;
import java.util.Map;

public class FileExtensionMimeMatcher {
    /*private static final Map<String, String> extensionToMimeMap = new HashMap<>();

    static {
        // 여기에 허용할 확장자와 그에 대응하는 MIME 유형을 추가하세요.

        extensionToMimeMap.put("pdf", "application/pdf");
        extensionToMimeMap.put("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        extensionToMimeMap.put("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation");
        extensionToMimeMap.put("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        extensionToMimeMap.put("ppt","application/vnd.ms-powerpoint");
        extensionToMimeMap.put("xls","application/vnd.ms-excel");
        extensionToMimeMap.put("doc","application/msword");
        extensionToMimeMap.put("zip","application/zip");
        extensionToMimeMap.put("hwp","application/octet-stream");


    }*/

    public static void isMatching(String  uploadMimeType, String tikaMimeType) throws FileUploadException {
        System.out.println(uploadMimeType);
        System.out.println(tikaMimeType);

        if(!uploadMimeType.equals(tikaMimeType)){

            throw new FileUploadException("Not Match MimeType");
        }
    }

/*    public static void main(String[] args) {
        String extension = "pdf";
        String mimeType = "application/pdf";

        if (isMatching(extension, mimeType)) {
            System.out.println("확장자와 MIME 유형이 일치합니다.");
        } else {
            System.out.println("확장자와 MIME 유형이 일치하지 않습니다.");
        }
    }*/
}
