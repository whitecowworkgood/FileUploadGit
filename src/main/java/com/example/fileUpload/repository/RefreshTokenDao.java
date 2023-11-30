package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.RefreshTokenMapperAnno;
import com.example.fileUpload.model.Token.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class RefreshTokenDao {

    private final SqlSessionTemplate sqlSession;

    @Autowired
    public RefreshTokenDao(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public Optional<RefreshToken> findByKey(String key) {
        return this.sqlSession.getMapper(RefreshTokenMapperAnno.class).findByKey(key);
    }

    public boolean existsByAccount(String rt_key) {
        return !this.sqlSession.getMapper(RefreshTokenMapperAnno.class).existsByAccount(rt_key).isEmpty();

    }

    public boolean removeRefreshTokenByValue(String value) {
        return this.sqlSession.getMapper(RefreshTokenMapperAnno.class).removeRefreshTokenByValue(value);
    }

    public void save(RefreshToken refreshToken) {
        this.sqlSession.getMapper(RefreshTokenMapperAnno.class).save(refreshToken);
    }

    public void update(String key, String signKey) {
        this.sqlSession.getMapper(RefreshTokenMapperAnno.class).update(key, signKey);
    }

    public String selectKey(String userName) {
        return this.sqlSession.getMapper(RefreshTokenMapperAnno.class).selectKey(userName);
    }
}