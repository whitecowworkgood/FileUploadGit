package com.example.fileUpload.JWT;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenValidate tokenValidate;

    @Override
    public void configure(HttpSecurity http) {
       JwtFilter customFilter = new JwtFilter(tokenValidate);
       http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
