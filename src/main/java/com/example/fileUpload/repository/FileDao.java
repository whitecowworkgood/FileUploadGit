package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.FileEntryMapperAnno;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.File.UserFileVO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

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
    public String printFileInfo(Long id, String userName){
        return sqlSession.getMapper(FileEntryMapperAnno.class).selectByIdName(id, userName);
    }

    public boolean saveFile(FileDto fileDto){
        return sqlSession.getMapper(FileEntryMapperAnno.class).insertFileEntity(fileDto);
    }

    public boolean deleteById(Long id){
        return sqlSession.getMapper(FileEntryMapperAnno.class).deleteFileEntry(id);
    }

    public List<FileVO> beforeAcceptFiles(){
        return sqlSession.getMapper(FileEntryMapperAnno.class).adminSelectView();
    }

    public void acceptFile(Long id){
        sqlSession.getMapper(FileEntryMapperAnno.class).updateIsActive(id);
    }

    public List<UserFileVO> acceptedFiles(String userName){
        return sqlSession.getMapper(FileEntryMapperAnno.class).acceptedFiles(userName);
    }

    public String selectUUIDFileNameByOriginalFileName(String userName, String FileName){
        return sqlSession.getMapper(FileEntryMapperAnno.class).selectUUIDFileNameByOriginalFileName(userName, FileName);
    }

    public UserFileVO acceptedFilesById(Long id){
        return sqlSession.getMapper(FileEntryMapperAnno.class).acceptedFileById(id);
    }

    public void decreaseCountNum(Long id){
        sqlSession.getMapper(FileEntryMapperAnno.class).updateCountNum(id);
    }
}