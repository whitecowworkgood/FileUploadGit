package com.example.fileUpload.Mybatis;

import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.File.UserFileVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FileEntityMapperAnno {
    @Select("SELECT id, uuidfile_name, file_ole_path," +
            " file_save_path,file_size,origin_file_name, file_type, count_num, user_name,comment,time_stamp, is_encrypt, is_active FROM file_entity where id = #{id}")
    @Results({
            @Result(column ="id", property="id"),
            @Result(column ="uuidfile_name", property="UUIDFileName"),
            @Result(column ="file_ole_path", property = "fileOlePath"),
            @Result(column ="file_save_path",property = "fileSavePath"),
            @Result(column = "file_size", property = "fileSize"),
            @Result(column ="origin_file_name", property="originalFileName"),
            @Result(column ="file_type", property="fileType"),
            @Result(column = "count_num", property = "countNum"),
            @Result(column = "user_name", property = "userName"),
            @Result(column = "comment", property = "comment"),
            @Result(column = "time_stamp",property = "timeStamp"),
            @Result(column = "is_encrypt", property = "isEncrypt"),
            @Result(column = "is_active", property = "isActive")
    })
    FileVO selectById(Long id);

    @Select("SELECT id, uuidfile_name, file_ole_path," +
            " file_save_path,file_size,origin_file_name, file_type, count_num, user_name,comment,time_stamp, is_encrypt, is_active"+
            " FROM file_entity where id = #{id} AND user_name = #{userName}")
    FileVO selectByIdName(Long id, String userName);

    @Delete("Delete from file_entity where id = #{id}")
    boolean deleteFileEntry(Long id);


    @Insert("INSERT INTO file_entity (uuidfile_name, file_ole_path, file_save_path, file_size, file_type, origin_file_name, count_num, user_name, comment, is_encrypt) " +
            "VALUES (#{UUIDFileName}, #{fileOlePath}, #{fileSavePath}, #{fileSize}, #{fileType}, #{originFileName}, #{countNum}, #{userName}, #{comment}, DEFAULT)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean insertFileEntity(FileDto fileDto);


    @Select("SELECT id, uuidfile_name, file_ole_path," +
            "file_save_path, file_size, origin_file_name, file_type, count_num, user_name, comment, time_stamp, is_encrypt, is_active FROM file_entity where is_active = '0'")

    List<FileVO> adminSelectView();


    @Update("UPDATE file_entity SET is_active = 1 WHERE id = #{id}")
    void updateIsActive(Long id);


    @Select("SELECT file_size, origin_file_name, count_num, comment, time_stamp, is_encrypt FROM file_entity"+
            " where is_active = '1' AND count_num > 0 AND user_name = #{userName}")
    List<UserFileVO> acceptedFiles(String userName);


    @Select("SELECT file_size, origin_file_name, count_num, comment, time_stamp, is_encrypt FROM file_entity"+
            " where is_active = '1' AND count_num > 0 AND id = #{id}")
    UserFileVO acceptedFileById(Long id);

    @Update("UPDATE file_entity SET count_Num = count_Num- 1 WHERE id = #{id}")
    void updateCountNum(Long id);

}