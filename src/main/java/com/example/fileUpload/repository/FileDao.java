package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.FileEntryMapperAnno;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.File.UserFileVO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public class FileDao {
    private final ThreadLocal<SqlSession> sqlSessionThreadLocal;

    public FileDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionThreadLocal = ThreadLocal.withInitial(()-> sqlSessionFactory.openSession());
    }

    public FileVO printFileOne(Long id){
        return this.sqlSessionThreadLocal.get().getMapper(FileEntryMapperAnno.class).selectById(id);
    }
    public Optional<FileVO> printFileInfo(Long id, String userName){
        return Optional.ofNullable(this.sqlSessionThreadLocal.get().getMapper(FileEntryMapperAnno.class).selectByIdName(id, userName));
    }

    public boolean saveFile(FileDto fileDto){
        return this.sqlSessionThreadLocal.get().getMapper(FileEntryMapperAnno.class).insertFileEntity(fileDto);
    }

    public boolean deleteById(Long id){
        return this.sqlSessionThreadLocal.get().getMapper(FileEntryMapperAnno.class).deleteFileEntry(id);
    }

    public List<FileVO> beforeAcceptFiles(){
        return this.sqlSessionThreadLocal.get().getMapper(FileEntryMapperAnno.class).adminSelectView();
    }

    public void acceptFile(Long id){
        this.sqlSessionThreadLocal.get().getMapper(FileEntryMapperAnno.class).updateIsActive(id);
    }

    public List<UserFileVO> acceptedFiles(String userName){
        return this.sqlSessionThreadLocal.get().getMapper(FileEntryMapperAnno.class).acceptedFiles(userName);
    }

    public UserFileVO acceptedFilesById(Long id){
        return this.sqlSessionThreadLocal.get().getMapper(FileEntryMapperAnno.class).acceptedFileById(id);
    }

    public void decreaseCountNum(Long id){
        this.sqlSessionThreadLocal.get().getMapper(FileEntryMapperAnno.class).updateCountNum(id);
    }
}