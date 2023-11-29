package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.TestMapperAnno;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.TestVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class TestDao {

    private final SqlSessionTemplate sqlSession;

    public TestDao(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public synchronized void insertTest(FileDto fileDto){
        this.sqlSession.getMapper(TestMapperAnno.class).save(fileDto);
    }

    public synchronized void updateStatusCode(String key){
        this.sqlSession.getMapper(TestMapperAnno.class).updateStatusCode(key);
    }

    public synchronized void updateStatusCodeComplete(String key){
        this.sqlSession.getMapper(TestMapperAnno.class).updateStatusCodeComplete(key);
    }

    public synchronized TestVO selectStatusCode(String code){
        return this.sqlSession.getMapper(TestMapperAnno.class).selectStatusCode(code);
    }
}
