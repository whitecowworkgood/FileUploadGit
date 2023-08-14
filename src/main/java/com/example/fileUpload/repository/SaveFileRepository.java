package com.example.fileUpload.repository;

import com.example.fileUpload.dto.FileDto;
import com.example.fileUpload.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SaveFileRepository extends JpaRepository<FileEntity, Long>{

}
