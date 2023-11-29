package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.FileEntryMapperAnno;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.File.UserFileVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public class FileDao {
    private final SqlSessionTemplate sqlSession;

    public FileDao(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public FileVO printFileOne(Long id){
        return this.sqlSession.getMapper(FileEntryMapperAnno.class).selectById(id);
    }
    public Optional<FileVO> printFileInfo(Long id, String userName){
        return Optional.ofNullable(this.sqlSession.getMapper(FileEntryMapperAnno.class).selectByIdName(id, userName));
    }

    public boolean saveFile(FileDto fileDto){
        return this.sqlSession.getMapper(FileEntryMapperAnno.class).insertFileEntity(fileDto);
    }

    public boolean deleteById(Long id){
        return this.sqlSession.getMapper(FileEntryMapperAnno.class).deleteFileEntry(id);
    }

    public List<FileVO> beforeAcceptFiles(){
        return this.sqlSession.getMapper(FileEntryMapperAnno.class).adminSelectView();
    }

    public void acceptFile(Long id){
        this.sqlSession.getMapper(FileEntryMapperAnno.class).updateIsActive(id);
    }

    public List<UserFileVO> acceptedFiles(String userName){
        return this.sqlSession.getMapper(FileEntryMapperAnno.class).acceptedFiles(userName);
    }

    public UserFileVO acceptedFilesById(Long id){
        return this.sqlSession.getMapper(FileEntryMapperAnno.class).acceptedFileById(id);
    }

    public void decreaseCountNum(Long id){
        this.sqlSession.getMapper(FileEntryMapperAnno.class).updateCountNum(id);
    }
}