package com.example.fileUpload.documentParser.module.OleExtractor;

import com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory.*;
import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import lombok.NoArgsConstructor;
import org.apache.poi.openxml4j.opc.PackagePart;

@NoArgsConstructor
public class OleExtractorFactory {

    public OleExtractor createOleExtractor(PackagePart pPart, FileDto fileDto) throws Exception {

        switch(pPart.getContentType()){

            case "application/vnd.ms-excel"->{
                return new ExcelExtractor(pPart, fileDto);
            }
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"->{
                return new XExcelExtractor(pPart, fileDto);
            }
            case "application/vnd.ms-excel.sheet.macroEnabled.12"->{
                return new CSVExtractor(pPart, fileDto);
            }
            case "application/msword"->{
                return new WordExtractor(pPart, fileDto);
            }
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document"->{
                return new XWordExtractor(pPart, fileDto);
            }
            case "application/vnd.ms-powerpoint"->{
                return new PowerPointExtractor(pPart, fileDto);
            }
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation"->{
                return new XPowerPointExtractor(pPart, fileDto);
            }
            default -> {
                return new OctExtractor(pPart, fileDto);
            }
        }

    }

    public static OleExtractor choiceExtractor(PackagePart pPart, FileDto fileDto) throws Exception {
        OleExtractorFactory oleExtractorFactory = new OleExtractorFactory();
        return oleExtractorFactory.createOleExtractor(pPart, fileDto);
    }
}
