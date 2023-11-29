package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.FileEncryptMapperAnno;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EncryptDao{


    private final SqlSessionTemplate sqlSession;

    @Autowired
    public EncryptDao(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public String findPrivateKey(long index){
        return this.sqlSession.getMapper(FileEncryptMapperAnno.class).findPrivateKey(index);
    }

    public void saveRSAKey(ConcurrentHashMap<String, String> stringKeypair){
        this.sqlSession.getMapper(FileEncryptMapperAnno.class).insertRSAKeys(stringKeypair);
    }
}
