package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.MemberMapperAnno;
import com.example.fileUpload.model.Member.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class MemberDao {

    private final ThreadLocal<SqlSession> sqlSessionThreadLocal;

    public MemberDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionThreadLocal = ThreadLocal.withInitial(()-> sqlSessionFactory.openSession());
    }

    public Optional<Member> findByAccount(String account){
        return this.sqlSessionThreadLocal.get().getMapper(MemberMapperAnno.class).findByAccount(account);
    }

    public boolean existsByAccount(String account){
        int num = this.sqlSessionThreadLocal.get().getMapper(MemberMapperAnno.class).existsByAccount(account);
        return num != 0;
    }

    public boolean save(Member member){

        return this.sqlSessionThreadLocal.get().getMapper(MemberMapperAnno.class).save(member);
    }
}
