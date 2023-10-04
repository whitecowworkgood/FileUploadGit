package com.example.fileUpload.Mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;

public interface FileEncryptMapperAnno {

    @Select("<script>"
            + "SELECT private_key FROM rsa_keys WHERE public_key = #{argPublicKey}"
            + "</script>")
    String findPrivateKey(String argPublicKey);


    @Insert("<script>"+
            "INSERT INTO rsa_keys (public_key, private_key) " +
            "VALUES (#{publicKey}, #{privateKey})"
            + "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRSAKeys(HashMap<String, String> stringKeypair);

}
