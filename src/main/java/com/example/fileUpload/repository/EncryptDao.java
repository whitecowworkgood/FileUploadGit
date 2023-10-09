package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.FileEncryptMapperAnno;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class EncryptDao {
    public final SqlSession sqlSession;

    public String findPrivateKey(long index){
        return sqlSession.getMapper(FileEncryptMapperAnno.class).findPrivateKey(index);
    }

    public void saveRSAKey(ConcurrentHashMap<String, String> stringKeypair){
        sqlSession.getMapper(FileEncryptMapperAnno.class).insertRSAKeys(stringKeypair);
    }
}
