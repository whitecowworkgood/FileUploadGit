/*
package com.example.fileUpload.Mybatis;

import com.example.fileUpload.dto.OleDto;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OleEntryMapperAnno {

    @Select("SELECT * FROM ole_entry WHERE super_id = #{id}")
*/
/*    @Results({
            @Result(column ="id", property="id"),
            @Result(column ="uuidfile_name", property="UUIDFileName"),
            @Result(column ="original_file_name", property="originalFileName"),
            @Result(column ="super_id", property="superId")
    })*//*

    List<OleDto> selectById(Long id);

    @Select("SELECT * FROM ole_entry")
    */
/*@Results({
            @Result(column ="id", property="id"),
            @Result(column ="uuidfile_name", property="UUIDFileName"),
            @Result(column ="original_file_name", property="originalFileName"),
            @Result(column ="super_id", property="superId")
    })*//*

    List<OleDto> findAll();
}*/
