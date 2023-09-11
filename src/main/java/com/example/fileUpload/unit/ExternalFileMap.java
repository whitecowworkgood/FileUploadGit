package com.example.fileUpload.unit;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ExternalFileMap {

    @Getter
    private static Map<String, String> fileNameMap = new HashMap<>();
    private static Map<String, Integer> fileNameCountMap = new HashMap<>();

    public static String addUniqueFileNameMapping(String originalFileName) {
        // 파일 이름에서 확장자를 추출
        String extension = getFileExtension(originalFileName);
        String randomName = UUID.randomUUID()+"."+extension;

        // 해당 확장자에 대한 카운트 가져오기
        Integer count = fileNameCountMap.get(originalFileName);

        // 만약 해당 확장자에 대한 카운트가 없다면 1로 초기화
        if (count == null) {
            count = 1;
        } else {
            // 같은 확장자에 대한 카운트가 이미 있으면 +1
            count++;
        }

        // 카운트 업데이트
        fileNameCountMap.put(originalFileName, count);

        // 파일 이름 생성

        if (count > 1) {
            originalFileName = removeFileExtension(originalFileName) + --count + "." + extension;
        }



        // 맵에 저장하고 반환
        fileNameMap.put(originalFileName, randomName);

        return randomName;
    }

    public static void addFileNameMapping(String originalFileName, String randomFileName) {
        fileNameMap.put(originalFileName, randomFileName);
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    private static String removeFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    public static void forEach(Consumer<? super Map.Entry<String, String>> action) {
        fileNameMap.entrySet().forEach(action);
    }
    public static void resetMap(){
        fileNameMap.clear();
        fileNameCountMap.clear();
    }

}
