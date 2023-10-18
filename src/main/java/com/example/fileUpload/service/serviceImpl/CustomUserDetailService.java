package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.User.MemberDto;
import com.example.fileUpload.repository.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailService implements UserDetailsService{

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userAccount) throws UsernameNotFoundException {

        return userDao.findByUserAccount(userAccount)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(MemberDto memberDto){

        return User.builder()
                .username(memberDto.getUsername())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                //.authorities(memberDto.getAuthorities())
                .roles(memberDto.getRole().toArray(new String[0]))
                .build();
    }
}
