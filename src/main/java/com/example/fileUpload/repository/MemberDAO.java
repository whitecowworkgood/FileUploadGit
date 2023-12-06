package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.MemberMapperAnno;
import com.example.fileUpload.model.Member.Member;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class MemberDAO {

    private final SqlSessionTemplate sqlSession;

    public MemberDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public Optional<Member> findByAccount(String account){
        return this.sqlSession.getMapper(MemberMapperAnno.class).findByAccount(account);
    }

    public boolean existsByAccount(String account){
        int num = this.sqlSession.getMapper(MemberMapperAnno.class).existsByAccount(account);
        return num != 0;
    }

    public boolean save(Member member){

        return this.sqlSession.getMapper(MemberMapperAnno.class).save(member);
    }
}
