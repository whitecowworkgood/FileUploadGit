package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.TestVO;
import org.apache.ibatis.annotations.*;

public interface TestMapperAnno {
    @Insert("insert into test (file_path, status_code, ole_path, key_value) VALUES (#{fileSavePath}, DEFAULT, #{fileOlePath}, #{comment})")
    @Results({
            @Result(column = "file_path", property = "fileSavePath"),
            @Result(column = "ole_path", property = "fileOlePath"),
            @Result(column = "key_value", property = "comment"),
            @Result(column = "status_code", property = "userName")
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(FileDto fileDto);

    @Update("UPDATE test SET status_code='processing' WHERE key_value = #{key}")
    void updateStatusCode(String key);

    @Update("UPDATE test SET status_code='complete' WHERE key_value = #{key}")
    void updateStatusCodeComplete(String key);


    @Select("SELECT status_code,ole_path from test where key_value=#{code}")
    TestVO selectStatusCode(String code);
}
