package com.example.fileUpload.unit;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ExternalFileMap {

    @Getter
    private static Map<String, String> fileNameMap = new HashMap<>();

    public static void addFileNameMapping(String originalFileName, String randomFileName) {
        fileNameMap.put(originalFileName, randomFileName);
    }
    public static String getMapValue(String originalFilName){
        return fileNameMap.get(originalFilName);
    }

    public static void forEach(Consumer<? super Map.Entry<String, String>> action) {
        fileNameMap.entrySet().forEach(action);
    }

}
