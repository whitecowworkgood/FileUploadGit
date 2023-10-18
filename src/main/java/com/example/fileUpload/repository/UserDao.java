package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.MemberMapperAnno;
import com.example.fileUpload.model.User.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDao {

    private final SqlSession sqlSession;

    public Optional<MemberDto> findByUserAccount(String MemberAccount){
        return sqlSession.getMapper(MemberMapperAnno.class).findByMemberAccount(MemberAccount);
    }
}
