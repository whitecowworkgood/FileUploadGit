package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileUtil {

    //private static final Tika tika = new Tika();
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


//            String mimeType = tika.detect(inputStream);
//            System.out.println(mimeType);
//            boolean isValid = validTypeList.stream().anyMatch(notValidType
//                    -> notValidType.equalsIgnoreCase(mimeType));
//
//            return isValid;
//        }catch(IOException e){
//            e.printStackTrace();
//            return false;
//        }
    }

    public static void fileOleParser(){

    }
}

