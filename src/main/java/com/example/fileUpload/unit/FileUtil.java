package com.example.fileUpload.unit;

import com.example.fileUpload.dto.FileDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class FileUtil {

    public static boolean valuedDocFile(FileDto fileDto){
        List<String> validTypeList = List.of("application/pdf","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "application/msword");

        if(validTypeList.contains(fileDto.getFileType())){
            return true;
        }

        return false;
    }

    public static boolean isValidPath(String defaultPath, String savePath){
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

            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    public static void fileOleParser(InputStream inputStream) throws IOException {

//        if (!file.isEmpty()) {
//            try (InputStream inputStream = file.getInputStream()){

                byte[] bytes = new byte[1024]; // heap에 만들어지는 것은 0으로 자동 초기화된다.
                int len = 0;

                len = inputStream.read(bytes, 0, 5);

                for (int i = 0; i<len; i++) {
                    System.out.printf("%x \n", bytes[i]);
                }
                //inputStream.close();

//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

    }

    private FileUtil() {
    }
}

