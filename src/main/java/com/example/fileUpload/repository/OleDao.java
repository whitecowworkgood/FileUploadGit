package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.OleEntryMapperAnno;
import com.example.fileUpload.model.Ole.OleDto;
import com.example.fileUpload.model.Ole.OleVO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OleDao {
    private final ThreadLocal<SqlSession> sqlSessionThreadLocal;

    public OleDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionThreadLocal = ThreadLocal.withInitial(()-> sqlSessionFactory.openSession());
    }

    public List<OleVO> selectById(Long id){
        return this.sqlSessionThreadLocal.get().getMapper(OleEntryMapperAnno.class).selectById(id);
    }


    public Boolean deleteById(Long id){
        return sqlSessionThreadLocal.get().getMapper(OleEntryMapperAnno.class).deleteOleEntry(id);
    }

    public boolean saveOle(OleDto oleDto){
        return sqlSessionThreadLocal.get().getMapper(OleEntryMapperAnno.class).insertOleEntry(oleDto);
    }
}
