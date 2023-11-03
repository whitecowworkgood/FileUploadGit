package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.MemberMapperAnno;
import com.example.fileUpload.Mybatis.RefreshTokenMapperAnno;
import com.example.fileUpload.model.Token.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenDao {
    public final SqlSession sqlSession;

    public Optional<RefreshToken> findByKey(String key){
        return sqlSession.getMapper(RefreshTokenMapperAnno.class).findByKey(key);
    }

    public boolean existsByAccount(String rt_key){
        int num = sqlSession.getMapper(RefreshTokenMapperAnno.class).existsByAccount(rt_key);
        return num != 0;
    }

    public boolean removeRefreshTokenByValue(String value){

        return sqlSession.getMapper(RefreshTokenMapperAnno.class).removeRefreshTokenByValue(value);
    }

    public void save(RefreshToken refreshToken){

        sqlSession.getMapper(RefreshTokenMapperAnno.class).save(refreshToken);

    }

    public String selectKey(String userName){
        return sqlSession.getMapper(RefreshTokenMapperAnno.class).selectKey(userName);
    }

}
