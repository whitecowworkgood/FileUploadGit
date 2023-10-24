package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.RefreshTokenMapperAnno;
import com.example.fileUpload.model.Token.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenDao {
    public final SqlSession sqlSession;

    public Optional<RefreshToken> findByKey(String key){
        return sqlSession.getMapper(RefreshTokenMapperAnno.class).findByKey(key);
    }

    public boolean removeRefreshTokenByValue(String value){

        return sqlSession.getMapper(RefreshTokenMapperAnno.class).removeRefreshTokenByValue(value);
    }

    public void save(RefreshToken refreshToken){
        sqlSession.getMapper(RefreshTokenMapperAnno.class).save(refreshToken);
    }

    public void update(RefreshToken refreshToken){
        sqlSession.getMapper(RefreshTokenMapperAnno.class).update(refreshToken);
    }

}
