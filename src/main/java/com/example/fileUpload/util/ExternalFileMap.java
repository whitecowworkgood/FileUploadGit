package com.example.fileUpload.util;

import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ExternalFileMap {

    @Getter
    private static ConcurrentHashMap<String, String> fileNameMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Integer> fileNameCountMap = new ConcurrentHashMap<>();

    /**
     * 추출할 OLE파일들의 이름을 가져와 UUID로 랜덤이름을 생성하고, MAP에 넣어서 관리합니다.
     *
     * @param originalFileName 추출할 OLE파일들의 실제 이름
     * @return randomName 원본명과 매치된 UUID이름을 반환합니다.
     * */
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
            originalFileName = removeFileExtension(originalFileName) +"_"+ --count + "." + extension;
        }



        // 맵에 저장하고 반환
        fileNameMap.put(originalFileName, randomName);

        return randomName;
    }

    /**
     * 원본명과 UUID이름을 MAP에 저장합니다.
     *
     * @param originalFileName 원본명을 가져옵니다.
     * @param randomFileName UUID로 만들어진 이름을 가져옵니다.
     * */
    public static void addFileNameMapping(String originalFileName, String randomFileName) {
        fileNameMap.put(originalFileName, randomFileName);
    }

    /**
     * 파일명에서 '.'을 기준으로 나눠 확장자를 가져옵니다.
     *
     * @param fileName 파일이름을 가져옵니다.
     * @return 확장자를 반환합니다.
     * */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * 파일명에서 '.'을 기준으로 나눠 파일명을 가져옵니다.
     *
     * @param fileName 파일이름을 가져옵니다.
     * @return 파일명을 반환합니다.
     * */
    private static String removeFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    /**
     * 클래스에 있는 forEach를 수행합니다.
     *
     * @param action forEach를 수행하는 map을 가져옵니다.
     * */
    public static void forEach(Consumer<? super Map.Entry<String, String>> action) {
        fileNameMap.entrySet().forEach(action);
    }

    /**
     * 클래스 내부에 있는 map을 초기화 합니다.
     * */
    public static void resetMap(){
        fileNameMap.clear();
        fileNameCountMap.clear();
    }

}
