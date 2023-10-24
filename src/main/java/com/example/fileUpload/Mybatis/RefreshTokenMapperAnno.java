package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.Token.RefreshToken;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

public interface RefreshTokenMapperAnno {

    @Select("SELECT rt_key, rt_value FROM refresh_token WHERE rt_key = #{key}")
    @Results({
            @Result(column ="rt_key", property="key"),
            @Result(column ="rt_value", property="value"),


    })
    Optional<RefreshToken> findByKey(String key);

    @Delete("DELETE FROM refresh_token where rt_value = #{value}")
    boolean removeRefreshTokenByValue(String value);


    @Insert("insert into refresh_token (rt_key, rt_value) VALUES (#{key}, #{value})")
    void save(RefreshToken refreshToken);

    @Update("UPDATE refresh_token SET rt_value = #{value} WHERE rt_key = #{key}")
    void update (RefreshToken refreshToken);
}
