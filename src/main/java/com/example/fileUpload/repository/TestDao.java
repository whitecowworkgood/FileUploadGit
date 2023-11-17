package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.TestMapperAnno;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.TestVO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class TestDao {
    private final SqlSession sqlSession;

    public synchronized void insertTest(FileDto fileDto){
        sqlSession.getMapper(TestMapperAnno.class).save(fileDto);
    }

    public synchronized void updateStatusCode(String key){
        sqlSession.getMapper(TestMapperAnno.class).updateStatusCode(key);
    }

    public synchronized void updateStatusCodeComplete(String key){
        sqlSession.getMapper(TestMapperAnno.class).updateStatusCodeComplete(key);
    }

    public synchronized TestVO selectStatusCode(String code){
        return sqlSession.getMapper(TestMapperAnno.class).selectStatusCode(code);
    }
}
