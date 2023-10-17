package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.MemberMapperAnno;
import com.example.fileUpload.model.members.Member;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberDao {
    public final SqlSession sqlSession;
    public boolean save(Member memberInfo){
        return sqlSession.getMapper(MemberMapperAnno.class).insertMemberInfo(memberInfo);
    }
    public Optional<Member> findOneWithAuthoritiesByUsername(String username){
        return sqlSession.getMapper(MemberMapperAnno.class).findOneWithAuthoritiesByUsername(username);
    }

}
