package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.FileEncryptMapperAnno;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
@RequiredArgsConstructor
public class EncryptDao {
    public final SqlSession sqlSession;

    public String findPrivateKey(String argPublicKey){
        return sqlSession.getMapper(FileEncryptMapperAnno.class).findPrivateKey(argPublicKey);
    }

    public void saveRSAKey(HashMap<String, String> stringKeypair){
        sqlSession.getMapper(FileEncryptMapperAnno.class).insertRSAKeys(stringKeypair);
    }
}
