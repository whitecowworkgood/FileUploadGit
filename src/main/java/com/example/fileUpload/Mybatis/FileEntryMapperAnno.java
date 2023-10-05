package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.model.FileVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FileEntryMapperAnno {
    @Select("SELECT id, uuidfile_name, file_ole_path," +
            "file_save_path, file_size, origin_file_name, file_type FROM file_entity where id = #{id}")
    @Results({
            @Result(column ="id", property="id"),
            @Result(column ="uuidfile_name", property="UUIDFileName"),
            @Result(column ="file_ole_path", property = "fileOlePath"),
            @Result(column ="file_save_path",property = "fileSavePath"),
            @Result(column = "file_size", property = "fileSize"),
            @Result(column ="origin_file_name", property="originalFileName"),
            @Result(column ="file_type", property="fileType")
    })
    FileVO selectById(Long id);

    @Select("SELECT id, uuidfile_name, file_ole_path," +
            "file_save_path,file_size,origin_file_name, file_type FROM file_entity")

    List<FileVO> findAllEntry();

    @Delete("Delete from file_entity where id = #{id}")
    boolean deleteFileEntry(Long id);

    @Insert("INSERT INTO file_entity (uuidfile_name, file_ole_path, file_save_path, file_size, file_type, origin_file_name) " +
            "VALUES (#{UUIDFileName}, #{fileOlePath}, #{fileSavePath}, #{fileSize}, #{fileType}, #{originFileName})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean insertFileEntity(FileDto fileDto);

}