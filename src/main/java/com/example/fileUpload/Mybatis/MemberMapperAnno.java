package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.members.Member;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

public interface MemberMapperAnno {

    @Insert("INSERT INTO member (username, password, nickname, email, real_name, roles, activated) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{email}, #{realName}, #{roles}, #{activated})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean insertMemberInfo(Member memberInfo);


    @Select("SELECT id, username, password, nickname, email, real_name, roles, activated " +
            "FROM member " +
            "WHERE username = #{username}")
    Optional<Member> findOneWithAuthoritiesByUsername(String username);

}
