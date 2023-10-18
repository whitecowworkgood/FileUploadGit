package com.example.fileUpload.service;

import com.example.fileUpload.model.TokenInfo;

public interface MemberService {
    TokenInfo login(String memberAccount, String password);
}
