package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.User.MemberDto;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

public interface MemberMapperAnno {

    @Select("SELECT member_account, password FROM members WHERE member_account = #{memberId}")
    @Results({
            @Result(property = "memberAccount", column = "member_account"),
            @Result(property = "password", column = "password"),
            @Result(property = "role", column = "member_account", javaType = List.class, many = @Many(select = "getRolesByMemberId"))
    })
    Optional<MemberDto> findByMemberAccount(String memberAccount);

    @Select("SELECT role FROM member_roles WHERE member_account = #{memberAccount}")
    List<String> getRolesByMemberId(String memberAccount);
}
