package com.example.fileUpload.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class FileUtil {


    public static String getFileExtension(String originalFileName) {
        if (originalFileName != null && originalFileName.contains(".")) {
            return "."+StringUtils.getFilenameExtension(originalFileName);
        } else {
            return ".bin"; // 확장자가 없을 경우 bin 문자열 반환
        }
    }

    public static String removePath(String filePath){
        String fileName = null;
        int lastSlashIndex = filePath.lastIndexOf("/");


        if (lastSlashIndex != -1 && lastSlashIndex < filePath.length() - 1) {
            fileName= filePath.substring(lastSlashIndex + 1);

        }
        return fileName;
    }


    public static String removeFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }
    /*
    public static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 서브폴더인 경우 재귀 호출로 서브폴더 삭제
                        deleteFolder(file);
                    } else {
                        // 파일인 경우 삭제
                        file.delete();
                    }
                }
            }
        }
    }*/


}

