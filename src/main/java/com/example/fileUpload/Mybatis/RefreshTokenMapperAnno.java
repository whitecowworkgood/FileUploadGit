package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.Token.RefreshToken;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

public interface RefreshTokenMapperAnno {

    @Select("SELECT rt_key, rt_value FROM refresh_token WHERE rt_key = #{key}")
    @Results({
            @Result(column ="rt_key", property="key"),
            @Result(column ="rt_value", property="value"),
            @Result(column = "signature_Key", property = "signatureKey")

    })
    Optional<RefreshToken> findByKey(String key);

    @Select("SELECT rt_key FROM refresh_token WHERE rt_key = #{rt_key} ")
    Optional<String> existsByAccount(String rt_key);

    @Delete("DELETE FROM refresh_token where rt_value = #{value}")
    boolean removeRefreshTokenByValue(String value);


    @Insert("insert into refresh_token (rt_key, rt_value, signature_Key) VALUES (#{key}, #{value}, #{signatureKey})")
    void save(RefreshToken refreshToken);

    @Update("UPDATE refresh_token SET signature_Key = #{signatureKey} WHERE rt_key = #{key}")
    void update(String key, String signKey);


    @Select("SELECT signature_Key FROM refresh_token WHERE rt_key = #{userName}")
    String selectKey(String userName);
}
