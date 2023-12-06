package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.RSAKeysMapperAnno;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RSAKeysDAO {


    private final SqlSessionTemplate sqlSession;

    @Autowired
    public RSAKeysDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public String findPrivateKey(long index){
        return this.sqlSession.getMapper(RSAKeysMapperAnno.class).findPrivateKey(index);
    }

    public void saveRSAKey(ConcurrentHashMap<String, String> stringKeypair){
        this.sqlSession.getMapper(RSAKeysMapperAnno.class).insertRSAKeys(stringKeypair);
    }

    public long getLatestId(){
        return this.sqlSession.getMapper(RSAKeysMapperAnno.class).getLatestId();
    }
}
