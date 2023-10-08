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
    private String dir;

    public static void callGenerateFolderMethods(String dir){
        DirectoryChecker directoryChecker = new DirectoryChecker(dir);

        directoryChecker.generateOLEFolders();
        directoryChecker.generateDownloadFolders();
        directoryChecker.generateTempFolders();
    }

    public DirectoryChecker(String dir) {
        this.dir = dir;
    }

    private void generateTempFolders(){
        Path folder = Paths.get(dir+ File.separator+"temp");

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
        Path folder = Paths.get(dir+ File.separator+"download");

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
        Path folder = Paths.get(dir+ File.separator+"ole");

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

    public static void generateUserFolder(String userFolder){
        Path folder = Paths.get(userFolder);

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
