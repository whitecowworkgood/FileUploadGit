package com.example.fileUpload.unit;

import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileUtil {

    private static final Tika tika = new Tika();
    public static boolean valuedDocFile(InputStream inputStream){
        try{
            List<String> validTypeList = List.of("application/pdf");
            String mimeType = tika.detect(inputStream);

            boolean isValid = validTypeList.stream().anyMatch(notValidType
                    -> notValidType.equalsIgnoreCase(mimeType));

            return isValid;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }
}

