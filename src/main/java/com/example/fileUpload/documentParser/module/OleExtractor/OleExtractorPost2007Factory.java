package com.example.fileUpload.documentParser.module.OleExtractor;

import com.example.fileUpload.documentParser.module.OleExtractor.ExtractorPost2007.XExcelExtractor;
import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractorPost2007;

public class OleExtractorPost2007Factory {

    public OleExtractorPost2007 createOleExtractor(){
        //예시 나중에 swtich문 두기
        return new XExcelExtractor();
    }
}
