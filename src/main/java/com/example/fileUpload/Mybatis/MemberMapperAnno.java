/*
package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.User.Member;
import com.example.fileUpload.model.User.MemberVo;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;
import java.util.Set;

public interface MemberMapperAnno {

   */
/* @Select("SELECT username, password, nickname FROM users WHERE username = #{username}")
    @Results({
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "nickname", column = "nickname"),
            //@Result(property = "role", column = "member_account", javaType = List.class, many = @Many(select = "getRolesByMemberId"))
    })
    Optional<MemberDto> findByMemberAccount(String memberAccount);

    @Select("SELECT role FROM member_roles WHERE member_account = #{memberAccount}")
    List<String> getRolesByMemberId(String memberAccount);*//*


    //사용자 정보를 조회하는 쿼리 메서드
    @Select("SELECT u.username. u.password, u.nickname, u.activated" +
            "FROM users u " +
            "LEFT JOIN user_authority ua ON u.id = ua.user_id " +
            "LEFT JOIN authority a ON ua.authority_name = a.authority_name " +
            "WHERE u.username = #{username}")
    @Results({
            @Result(property = "userId", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "activated", column = "activated"),
            @Result(property = "role", javaType = Set.class, column = "authority_name", many = @Many(select = "getRolesByUserId"))
    })
    Optional<MemberVo> findUserWithAuthoritiesByUsername(String username);

    @Select("SELECT a.authority_name " +
            "FROM authority a " +
            "JOIN user_authority ua ON a.authority_name = ua.authority_name " +
            "WHERE ua.user_id = #{userId}")
    Set<String> getRolesByUserId(Long userId);
}
*/
