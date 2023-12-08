package com.example.fileUpload.documentParser.module.OleExtractor;

import com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory.*;
import com.example.fileUpload.model.File.FileDto;
import lombok.NoArgsConstructor;
import org.apache.poi.openxml4j.opc.PackagePart;

@NoArgsConstructor
public class OleExtractorFactory {

    public void createModernOleExtractor(PackagePart pPart, FileDto fileDto) throws Exception {

        switch(pPart.getContentType()){

            case "application/vnd.ms-excel"->{
                new ExcelExtractor(pPart, fileDto).doExtract();
                break;
            }
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"->{
                new XExcelExtractor(pPart, fileDto).doExtract();
                break;
            }
            case "application/vnd.ms-excel.sheet.macroEnabled.12"->{
                new CSVExtractor(pPart, fileDto).doExtract();
                break;
            }
            case "application/msword"->{
                new WordExtractor(pPart, fileDto).doExtract();
                break;
            }
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document"->{
                new XWordExtractor(pPart, fileDto).doExtract();
                break;
            }
            case "application/vnd.ms-powerpoint"->{
                new PowerPointExtractor(pPart, fileDto).doExtract();
                break;
            }
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation"->{
                new XPowerPointExtractor(pPart, fileDto).doExtract();
                break;
            }
            default -> {
                new OctExtractor(pPart, fileDto).doExtract();
                break;
            }
        }

    }

}
