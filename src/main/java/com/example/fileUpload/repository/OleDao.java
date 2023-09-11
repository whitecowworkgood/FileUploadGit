/*
package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.OleEntryMapperAnno;
import com.example.fileUpload.dto.OleDto;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OleDao {

    private final SqlSession sqlSession;

    public List<OleDto> selectById(Long id){
        return sqlSession.getMapper(OleEntryMapperAnno.class).selectById(id);
    }

    public List<OleDto> printAll(){
        return sqlSession.getMapper(OleEntryMapperAnno.class).findAll();
    }
}
*/
