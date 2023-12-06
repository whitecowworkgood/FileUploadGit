package com.example.fileUpload.Mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.concurrent.ConcurrentHashMap;

public interface RSAKeysMapperAnno {

    @Select("SELECT private_key FROM rsa_keys WHERE id = #{id}")
    String findPrivateKey(long index);


    @Insert("INSERT INTO rsa_keys (public_key, private_key) " +
            "VALUES (#{publicKey}, #{privateKey})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRSAKeys(ConcurrentHashMap<String, String> stringKeypair);

    @Select("SELECT id FROM rsa_keys ORDER BY id DESC LIMIT 1;")
    long getLatestId();

}
