package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.OleEntryMapperAnno;
import com.example.fileUpload.model.Ole.OleDto;
import com.example.fileUpload.model.Ole.OleVO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OleDao {

    private final SqlSession sqlSession;

    public List<OleVO> selectById(Long id){
        return sqlSession.getMapper(OleEntryMapperAnno.class).selectById(id);
    }


    public Boolean deleteById(Long id){
        return sqlSession.getMapper(OleEntryMapperAnno.class).deleteOleEntry(id);
    }

    public boolean insertOle(OleDto oleDto){
        return sqlSession.getMapper(OleEntryMapperAnno.class).insertOleEntry(oleDto);
    }
}
