package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.Ole.OleDto;
import com.example.fileUpload.model.Ole.OleVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OleEntryMapperAnno {

    @Select("SELECT uuidfile_name, original_file_name FROM ole_entry WHERE super_id = #{id}")
    @Results({
            @Result(column ="uuidfile_name", property="UUIDFileName"),
            @Result(column ="original_file_name", property="originalFileName"),
    })
    List<OleVO> selectById(Long id);


    @Insert("INSERT INTO ole_entry (uuidfile_name, original_file_name, super_id) " +
            "VALUES (#{UUIDFileName}, #{originalFileName}, #{superId})")
    boolean insertOleEntry(OleDto oleDto);



    @Delete("DELETE FROM ole_entry where super_id=#{id}")
    boolean deleteOleEntry(Long id);
}
