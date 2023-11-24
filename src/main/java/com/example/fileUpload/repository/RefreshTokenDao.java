package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.RefreshTokenMapperAnno;
import com.example.fileUpload.model.Token.RefreshToken;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RefreshTokenDao {
    private final ThreadLocal<SqlSession> sqlSessionThreadLocal;

    public RefreshTokenDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionThreadLocal = ThreadLocal.withInitial(()-> sqlSessionFactory.openSession());
    }

    public Optional<RefreshToken> findByKey(String key){
        return this.sqlSessionThreadLocal.get().getMapper(RefreshTokenMapperAnno.class).findByKey(key);
    }

    public boolean existsByAccount(String rt_key){
        int num = this.sqlSessionThreadLocal.get().getMapper(RefreshTokenMapperAnno.class).existsByAccount(rt_key);
        return num != 0;
    }

    public boolean removeRefreshTokenByValue(String value){

        return this.sqlSessionThreadLocal.get().getMapper(RefreshTokenMapperAnno.class).removeRefreshTokenByValue(value);
    }

    public void save(RefreshToken refreshToken){

        this.sqlSessionThreadLocal.get().getMapper(RefreshTokenMapperAnno.class).save(refreshToken);

    }

    public String selectKey(String userName){
        return this.sqlSessionThreadLocal.get().getMapper(RefreshTokenMapperAnno.class).selectKey(userName);
    }

}
