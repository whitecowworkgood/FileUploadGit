package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.members.Member;
import com.example.fileUpload.repository.MemberDao;
import com.example.fileUpload.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailService")
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailService {

    private final MemberDao memberDao;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        return memberDao.findOneWithAuthoritiesByUsername(username)
                .map(member -> createUser(username, member))
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    private String/*org.springframework.security.core.userdetails.User*/ createUser(String username, Member member) {
        if (!member.isActivated()) {
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
        }

        /*List<SimpleGrantedAuthority> grantedAuthorities = member.getRoles().name().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(member.getUsername(),
                member.getPassword(),
                grantedAuthorities);*/
        
        return member.getRoles().name();
    }
}
