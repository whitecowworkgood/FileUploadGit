package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.FileEncryptMapperAnno;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EncryptDao{

    private final ThreadLocal<SqlSession> sqlSessionThreadLocal;

    public EncryptDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionThreadLocal = ThreadLocal.withInitial(()-> sqlSessionFactory.openSession());
    }

    public String findPrivateKey(long index){
        //return sqlSession.getMapper(FileEncryptMapperAnno.class).findPrivateKey(index);
        return this.sqlSessionThreadLocal.get().getMapper(FileEncryptMapperAnno.class).findPrivateKey(index);
    }

    public void saveRSAKey(ConcurrentHashMap<String, String> stringKeypair){
        this.sqlSessionThreadLocal.get().getMapper(FileEncryptMapperAnno.class).insertRSAKeys(stringKeypair);
    }
}
