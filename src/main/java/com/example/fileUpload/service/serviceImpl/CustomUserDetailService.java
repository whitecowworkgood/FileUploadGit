package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.Member.Member;
import com.example.fileUpload.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByAccount(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException((username+" -> 데이터베이스에서 유저정보를 찾을 수 없습니다.")));
    }
    private UserDetails createUserDetails(Member member){
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());

        return new User(
                String.valueOf(member.getAccount()),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
