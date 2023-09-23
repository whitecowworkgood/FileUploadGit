package com.example.fileUpload.documentParser.module.OleExtractor;

import com.example.fileUpload.documentParser.module.OleExtractor.ExtractorPre2007.ExcelExtractor;
import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractorPre2007;

public class OleExtractorPre2007Factory {

    public OleExtractorPre2007 createOleExtractor(){
        //예시 나중에 swtich문 두기
        return new ExcelExtractor();

    }
}
