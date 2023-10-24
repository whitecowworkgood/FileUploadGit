package com.example.fileUpload.Mybatis;


import com.example.fileUpload.model.Member.Member;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

public interface MemberMapperAnno {

    @Select("SELECT member_id, account, authority, password FROM member where account = #{account}")
    @Results({
        @Result(column ="member_id", property="id"),
        @Result(column ="account", property="account"),
        @Result(column ="authority", property = "authority"),
        @Result(column ="password",property = "password")

    })
    Optional<Member> findByAccount(String account);


    @Select("SELECT COUNT(*) FROM member WHERE account = #{account}")
    int existsByAccount(String account);

    @Insert("INSERT INTO member (account, authority, password) VALUES (#{account}, #{authority}, #{password})")
    boolean save(Member member);



}

