package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.members.Authority;
import com.example.fileUpload.model.members.Member;
import com.example.fileUpload.repository.MemberDao;
import com.example.fileUpload.service.MemberService;
import com.example.fileUpload.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean signup(Member member) {
        if (memberDao.findOneWithAuthoritiesByUsername(member.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        Member memberInfo = Member.builder()
                .username(member.getUsername())
                .password(passwordEncoder.encode(member.getPassword()))
                .nickname(member.getNickname())
                .email(member.getEmail())
                .realName(member.getRealName())
                .roles(Collections.singleton(authority))
                .activated(true)
                .build();

        return memberDao.save(memberInfo);
    }

    // 유저,권한 정보를 가져오는 메소드
    @Transactional(readOnly = true)
    public Optional<Member> getUserWithAuthorities(String username) {
        return memberDao.findOneWithAuthoritiesByUsername(username);
    }

    // 현재 securityContext에 저장된 username의 정보만 가져오는 메소드
    @Transactional(readOnly = true)
    public Optional<Member> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername()
                .flatMap(memberDao::findOneWithAuthoritiesByUsername);
    }

}
