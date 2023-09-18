package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.OleDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OleEntryMapperAnno {

    @Select("SELECT id, uuidfile_name, original_file_name, super_id FROM ole_entry WHERE super_id = #{id}")
    @Results({
            @Result(column ="id", property="id"),
            @Result(column ="uuidfile_name", property="UUIDFileName"),
            @Result(column ="original_file_name", property="originalFileName"),
            @Result(column ="super_id", property="superId")
    })
    List<OleDto> selectById(Long id);

    @Select("SELECT id, uuidfile_name, original_file_name, super_id FROM ole_entry")
    @Results({
            @Result(column ="id", property="id"),
            @Result(column ="uuidfile_name", property="UUIDFileName"),
            @Result(column ="original_file_name", property="originalFileName"),
            @Result(column ="super_id", property="superId")
    })
    List<OleDto> findAllEntry();

    @Insert("INSERT INTO ole_entry (uuidfile_name, original_file_name, super_id) " +
            "VALUES (#{UUIDFileName}, #{originalFileName}, #{superId})")
    boolean insertOleEntry(OleDto oleDto);



    @Delete("DELETE FROM ole_entry where super_id=#{id}")
    boolean deleteOleEntry(Long id);
}
