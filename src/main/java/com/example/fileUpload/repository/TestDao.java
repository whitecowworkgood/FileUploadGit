package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.TestMapperAnno;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.TestVO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;


@Repository
public class TestDao {

    private final ThreadLocal<SqlSession> sqlSessionThreadLocal;

    public TestDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionThreadLocal = ThreadLocal.withInitial(()-> sqlSessionFactory.openSession());
    }

    public synchronized void insertTest(FileDto fileDto){
        this.sqlSessionThreadLocal.get().getMapper(TestMapperAnno.class).save(fileDto);
    }

    public synchronized void updateStatusCode(String key){
        this.sqlSessionThreadLocal.get().getMapper(TestMapperAnno.class).updateStatusCode(key);
    }

    public synchronized void updateStatusCodeComplete(String key){
        this.sqlSessionThreadLocal.get().getMapper(TestMapperAnno.class).updateStatusCodeComplete(key);
    }

    public synchronized TestVO selectStatusCode(String code){
        return this.sqlSessionThreadLocal.get().getMapper(TestMapperAnno.class).selectStatusCode(code);
    }
}
