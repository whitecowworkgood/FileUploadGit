package com.example.fileUpload.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class DirectoryChecker {

    @Value("${Save-Directory}")
    private String baseDir;

    public static void callGenerateFolderMethods(String dir){
        DirectoryChecker directoryChecker = new DirectoryChecker(dir);

        directoryChecker.generateOLEFolders();
        directoryChecker.generateDownloadFolders();
        directoryChecker.generateTempFolders();
    }

    public DirectoryChecker(String dir) {
        this.baseDir = dir;
    }

    private void generateTempFolders(){
        Path folder = Paths.get(this.baseDir+ File.separator+"temp");

        // 폴더 존재 여부 확인
        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            try {
                Files.createDirectories(folder);
                log.info("Folder created: " + folder);
            } catch (IOException e) {
                // 폴더 생성 실패에 대한 예외 처리
                ExceptionUtils.getStackTrace(e);

            }
        }
    }

    private void generateDownloadFolders(){
        Path folder = Paths.get(this.baseDir+ File.separator+"download");

        // 폴더 존재 여부 확인
        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            try {
                Files.createDirectories(folder);
                log.info("Folder created: " + folder);
            } catch (IOException e) {
                // 폴더 생성 실패에 대한 예외 처리
                ExceptionUtils.getStackTrace(e);

            }
        }
    }

    private void generateOLEFolders(){
        Path folder = Paths.get(this.baseDir+ File.separator+"ole");

        // 폴더 존재 여부 확인
        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            try {
                Files.createDirectories(folder);
                log.info("Folder created: " + folder);
            } catch (IOException e) {
                // 폴더 생성 실패에 대한 예외 처리
                ExceptionUtils.getStackTrace(e);

            }
        }
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
