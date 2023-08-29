package com.example.fileUpload.repository;

import com.example.fileUpload.entity.OleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SaveOleRepository extends JpaRepository<OleEntry, Long> {
    Optional<OleEntry> findBySuperId(Long id);
}