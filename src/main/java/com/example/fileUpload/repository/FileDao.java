package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.FileEntryMapperAnno;
import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.model.FileVO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FileDao {
    public final SqlSession sqlSession;

    public List<FileVO> printFileAll(){
        return sqlSession.getMapper(FileEntryMapperAnno.class).findAllEntry();
    }

    public FileVO printFileOne(Long id){
        return sqlSession.getMapper(FileEntryMapperAnno.class).selectById(id);
    }

    public boolean saveFile(FileDto fileDto){
        return sqlSession.getMapper(FileEntryMapperAnno.class).insertFileEntity(fileDto);
    }

    public boolean deleteById(Long id){
        return sqlSession.getMapper(FileEntryMapperAnno.class).deleteFileEntry(id);
    }
}