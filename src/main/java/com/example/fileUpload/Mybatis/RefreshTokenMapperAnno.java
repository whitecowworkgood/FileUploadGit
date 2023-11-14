package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.Token.RefreshToken;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

public interface RefreshTokenMapperAnno {

    @Select("SELECT rt_key, rt_value FROM refresh_token WHERE rt_key = #{key}")
    @Results({
            @Result(column ="rt_key", property="key"),
            @Result(column ="rt_value", property="value"),
            @Result(column = "signkey", property = "signkey")

    })
    Optional<RefreshToken> findByKey(String key);

    @Select("SELECT COUNT(*) FROM refresh_token WHERE rt_key = #{rt_key} ")
    int existsByAccount(String rt_key);

    @Delete("DELETE FROM refresh_token where rt_value = #{value}")
    boolean removeRefreshTokenByValue(String value);


    @Insert("insert into refresh_token (rt_key, rt_value, signkey) VALUES (#{key}, #{value}, #{signKey})")
    void save(RefreshToken refreshToken);


    @Select("SELECT signkey FROM refresh_token WHERE rt_key = #{userName}")
    String selectKey(String userName);
}
