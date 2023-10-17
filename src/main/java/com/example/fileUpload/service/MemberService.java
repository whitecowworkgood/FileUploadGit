package com.example.fileUpload.service;

import com.example.fileUpload.model.members.Member;

import java.util.Optional;

public interface MemberService {
    boolean signup(Member member);
    Optional<Member> getUserWithAuthorities(String username);
    Optional<Member> getMyUserWithAuthorities();
}
