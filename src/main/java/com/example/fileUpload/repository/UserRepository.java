package com.example.fileUpload.repository;

import com.example.fileUpload.repository.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
