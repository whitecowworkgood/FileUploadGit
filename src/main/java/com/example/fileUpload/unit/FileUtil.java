package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.util.*;

@Slf4j
public class FileUtil {


    public static boolean validateUploadedFileMimeType(FileDto fileDto){

        List<String> validTypeList = Arrays.stream(MimeType.values())
                .map(MimeType::getValue)
                .toList();

        return validTypeList.contains(fileDto.getFileType());
    }

    public static boolean isPathValidForStorage(String defaultPath, String savePath){
        if (defaultPath == null || savePath == null || defaultPath.isEmpty() || savePath.isEmpty()) {
            return false;
        }

        if (!savePath.startsWith(defaultPath)) {
            return false;
        }

        File saveFile = new File(savePath);
        File defaultDir = new File(defaultPath);

        try {

            String normalizedSavePath = saveFile.getCanonicalPath();
            String normalizedDefaultPath = defaultDir.getCanonicalPath();

            return normalizedSavePath.startsWith(normalizedDefaultPath);
        } catch (IOException e) {

            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
    public static List<String> folderSearch(String savePath){
        List<String> fileList = new ArrayList<>();

        File Folder = new File(savePath);

        File[] files = Folder.listFiles();

        for(File file : Objects.requireNonNull(files)){
            fileList.add(savePath+"\\"+file.getName());
        }
        return fileList;
    }

    public static String removeNullCharacters(String input) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != '\u0000') {
                output.append(c);
            }
        }

        return output.toString();
    }
    public static String getRtNum() {
        int nSeed;
        int nSeedSize = 62; // 숫자 + 영문
        String strSrc = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; // 숫자, 영문
        StringBuilder strKey = new StringBuilder();
        for(int i=0; i<12; i++){
            nSeed = (int)(Math.random() * nSeedSize) + 1;
            strKey.append(strSrc.charAt(nSeed - 1));
        }
        return strKey.toString();
    }

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
    }
    private FileUtil() {
    }
}

