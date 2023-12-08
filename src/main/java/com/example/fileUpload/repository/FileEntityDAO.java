package com.example.fileUpload.repository;

import com.example.fileUpload.Mybatis.FileEntityMapperAnno;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.File.UserFileVO;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public class FileEntityDAO {
    private final SqlSessionTemplate sqlSession;

    public FileEntityDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public FileVO printFileOne(Long id){
        return this.sqlSession.getMapper(FileEntityMapperAnno.class).selectById(id);
    }

    public Optional<FileVO> printFileInfo(Long id, String userName){
        return Optional.ofNullable(this.sqlSession.getMapper(FileEntityMapperAnno.class).selectByIdName(id, userName));
    }

    public Optional<Boolean> saveFile(FileDto fileDto) throws FileUploadException {
        return Optional.ofNullable(Optional.of(this.sqlSession.getMapper(FileEntityMapperAnno.class).insertFileEntity(fileDto))
                .orElseThrow(() -> new FileUploadException("파일 업로드에 실패하였습니다.")));
    }

    public List<FileVO> beforeAcceptFiles(){
        return this.sqlSession.getMapper(FileEntityMapperAnno.class).adminSelectView();
    }

    public void acceptFile(Long id){
        this.sqlSession.getMapper(FileEntityMapperAnno.class).updateIsActive(id);
    }

    public List<UserFileVO> acceptedFiles(String userName){
        return this.sqlSession.getMapper(FileEntityMapperAnno.class).acceptedFiles(userName);
    }

    public UserFileVO acceptedFilesById(Long id){
        return this.sqlSession.getMapper(FileEntityMapperAnno.class).acceptedFileById(id);
    }

    public void decreaseCountNum(Long id){
        this.sqlSession.getMapper(FileEntityMapperAnno.class).updateCountNum(id);
    }
}