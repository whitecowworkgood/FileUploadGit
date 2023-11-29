package com.example.fileUpload.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class DirectoryChecker {


    public static void checkAndCreateBasicFolder(String baseDir){

        generateFolder(baseDir+ File.separator+"ole");
        generateFolder(baseDir+ File.separator+"download");
        generateFolder(baseDir+ File.separator+"temp");
    }

    public static void generateFolder(String folderPath){
        Path folder = Paths.get(folderPath);

        // 폴더 존재 여부 확인
        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            try {
                Files.createDirectory(folder);
                log.info("Folder created: " + folder);
            } catch (IOException e) {
                // 폴더 생성 실패에 대한 예외 처리
                ExceptionUtils.getStackTrace(e);

            }
        }
    }

}
