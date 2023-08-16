package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileUtil {

    public static boolean valuedDocFile(FileDto fileDto){
        List<String> validTypeList = List.of("application/pdf","image/jpeg","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "application/msword");

        if (validTypeList.contains(fileDto.getFileType())){
            return true;

        }else{
            return false;
        }

    }

    public static void fileOleParser(){

    }

    private FileUtil() {
    }
}

