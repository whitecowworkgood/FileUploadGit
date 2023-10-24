package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.MemberMapperAnno;
import com.example.fileUpload.model.Member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberDao {
    public final SqlSession sqlSession;

    public Optional<Member> findByAccount(String account){
        return sqlSession.getMapper(MemberMapperAnno.class).findByAccount(account);
    }

    public boolean existsByAccount(String account){
        int num = sqlSession.getMapper(MemberMapperAnno.class).existsByAccount(account);
        return num != 0;
    }

    public boolean save(Member member){

        return sqlSession.getMapper(MemberMapperAnno.class).save(member);
    }
}
