package com.example.fileUpload.documentParser.module.OleExtractor;

import com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory.*;
import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.util.FileType;
import lombok.NoArgsConstructor;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.DirectoryNode;

@NoArgsConstructor
public class OleExtractorFactory {

    public OleExtractor createMordernOleExtractor(PackagePart pPart, FileDto fileDto) throws Exception {

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
                //return null;
            }
        }

    }
    /*public OleExtractor createLagacyOleExtractor(DirectoryNode directoryNode, FileDto fileDto) throws Exception {

        *//*switch(directoryNode.toString()){

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
        }*//*

    }*/

    public static OleExtractor choiceExtractor(PackagePart pPart, FileDto fileDto) throws Exception {
        OleExtractorFactory oleExtractorFactory = new OleExtractorFactory();
        return oleExtractorFactory.createMordernOleExtractor(pPart, fileDto);
    }

    /*public static OleExtractor choiceExtractor(DirectoryNode directoryNode, FileDto fileDto) throws Exception {
        OleExtractorFactory oleExtractorFactory = new OleExtractorFactory();
        return oleExtractorFactory.createLagacyOleExtractor(directoryNode, fileDto);
    }*/
}
