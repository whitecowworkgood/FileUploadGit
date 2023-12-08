package com.example.fileUpload.documentParser.parsers;

import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Path;

public class FileProcessorTest {

    public static File getParser(String savePath, String uuidName) throws IOException {

        Tika tika = new Tika();
        String mimeType = tika.detect(Path.of(savePath));

        switch (mimeType){
            case "application/msword",
                    "application/vnd.ms-powerpoint",
                    "application/vnd.ms-excel"-> {
                return new LegacyFile();

            }
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                   "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"-> {
                    return new ModernFile(savePath);
            }
            case "application/x-hwp-v5" -> {
                return null;
            }
            default -> {
                throw new RuntimeException("처리할 수 없는 타입의 MimeType입니다.");
            }
        }
    }

}
