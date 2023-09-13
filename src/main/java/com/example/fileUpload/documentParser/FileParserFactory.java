package com.example.fileUpload.documentParser;

import com.example.fileUpload.documentParser.parsers.*;
import com.example.fileUpload.documentParser.parsers.abstracts.FileParser;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Slf4j
@Component
public class FileParserFactory {

    /**
     * 업로드한 파일의 mime-type를 비교해서 알맞은 Parser를 호출합니다.
     *
     * @param fileName hwp파일에서 mime-type이 hwp와 hwpx가 같아서 확장자로 구분하기 위한 String
     * @param mimeType 업로드된 파일의 mime-type을 비교하여 알맞은 Parser를 호출하기 위한 String
     *
     * @return FileParser 알맞은 Parser를 반환합니다.
     * */
    public FileParser createParser(String mimeType, String fileName){
        //log.info(mimeType);
        switch (mimeType){
            case "application/vnd.ms-powerpoint"->{
                return new PowerPointParser();
            }
            case "application/vnd.ms-excel"->{
                return new ExcelParser();
            }
            case "application/msword"->{
                return new WordParser();
            }
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document"->{
                return new XWordParser();
            }
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation"->{
                return new XPowerPointParser();
            }
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ->{
                return new XExcelParser();
            }
            case "application/octet-stream" -> {
                if (fileName.equals(".hwpx")) {
                    return new XHwpParser();
                }
                return new HwpParser();
            }
            default -> {
                throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
            }
        }
    }
}
