package com.example.fileUpload.util;

import com.example.fileUpload.model.File.FileDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Slf4j
public class FileUtil {

    /**
     * 업로드한 파일이 mime-type을 비교합니다.
     *
     * @param fileDto 업로드 파일의 정보를 가지고 있는 DTO
     * @return mime-type을 처리하면 true, 아니면 false
     * */
    /*public static boolean validateUploadedFileMimeType(FileDto fileDto)throws RuntimeException{

        List<String> validTypeList = Arrays.stream(MimeType.values())
                .map(MimeType::getValue)
                .toList();

        if(!validTypeList.contains(fileDto.getFileType())){
            throw new RuntimeException();
        }

        return validTypeList.contains(fileDto.getFileType());
    }*/
    public static boolean validateUploadedFileMimeType(FileDto fileDto) {
        List<String> validTypeList = Arrays.stream(MimeType.values())
                .map(MimeType::getValue)
                .toList();

        return validTypeList.contains(fileDto.getFileType());
    }



    /**
     * 업로드 경로와, 저장 실제 경로가 같은지 비교합니다.
     *
     * @param defaultPath 스프링에 저장된 저장 경로입니다.
     * @param savePath 업로드시 저장된 경로입니다.
     * @return 두 경로가 같으면 true, 틀리면 false반환
     * */
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

            ExceptionUtils.getStackTrace(e);
            return false;
        }
    }

    /**
     * 파일의 확장자를 가져옵니다.
     *
     * @param file MultipartFile의 데이터를 가져옵니다.
     * @return 확장자를 반환합니다.
     * */
    public static String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null && originalFileName.contains(".")) {
            return "."+StringUtils.getFilenameExtension(originalFileName);
        } else {
            return ".bin"; // 확장자가 없을 경우 bin 문자열 반환
        }
    }
    public static String getFileExtension(String fileName) {

        if (fileName != null && fileName.contains(".")) {
            return "."+StringUtils.getFilenameExtension(fileName);
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
    /**
     * 폴더를 삭제합니다.
     *
     * @param folder 폴더에 대한 File타입의 값을 가져옴
     * */
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
}

