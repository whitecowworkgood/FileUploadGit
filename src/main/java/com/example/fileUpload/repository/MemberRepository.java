package com.example.fileUpload.repository;

import com.example.fileUpload.model.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAccount(String account);
    boolean existsByAccount(String account);
}
