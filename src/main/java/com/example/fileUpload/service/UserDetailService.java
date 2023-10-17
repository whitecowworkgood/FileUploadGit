package com.example.fileUpload.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailService {
    UserDetails loadUserByUsername(final String username);
}
