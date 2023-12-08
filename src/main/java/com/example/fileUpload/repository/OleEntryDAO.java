package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.OleEntryMapperAnno;
import com.example.fileUpload.model.Ole.OleDto;
import com.example.fileUpload.model.Ole.OleVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OleEntryDAO {
    private final SqlSessionTemplate sqlSession;

    public OleEntryDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public List<OleVO> selectById(Long id){
        return this.sqlSession.getMapper(OleEntryMapperAnno.class).selectById(id);
    }

    public boolean saveOle(OleDto oleDto){
        return sqlSession.getMapper(OleEntryMapperAnno.class).insertOleEntry(oleDto);
    }
}
