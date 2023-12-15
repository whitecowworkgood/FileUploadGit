package com.example.fileUpload.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class FileUtil {

    public static String getFileExtension(String originalFileName) {
        if (originalFileName != null && originalFileName.contains(".")) {
            return StringUtils.getFilenameExtension(originalFileName);

        } else {
            return "bin"; // 확장자가 없을 경우 bin 문자열 반환
        }
    }

    public static String removeFileExtension(String fileName) {

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }
}

