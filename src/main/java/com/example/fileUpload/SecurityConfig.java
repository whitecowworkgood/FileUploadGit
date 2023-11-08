package com.example.fileUpload;

import com.example.fileUpload.JWT.JwtAccessDeniedHandler;
import com.example.fileUpload.JWT.JwtAuthenticationEntryPoint;
import com.example.fileUpload.JWT.JwtSecurityConfig;
import com.example.fileUpload.JWT.TokenProvider;

import com.example.fileUpload.Security.MemberRequestDtoFilter;
import com.example.fileUpload.Security.MultipartUploadFilter;
import com.example.fileUpload.Security.XssProtectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final String[] allowedUrls = {"/swagger-ui/**", "/api-docs/**","/h2-console/**", "/auth/**"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.csrf().disable()
                .formLogin().disable()
                .addFilterBefore(new MemberRequestDtoFilter("/auth/login", "/auth/signup"), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new XssProtectFilter("/api/**"), corsFilter.getClass())
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)//JsonDataFilter.class
                .addFilterAfter(new MultipartUploadFilter("/api/upload"), UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeHttpRequests(requests ->
                        requests.requestMatchers(allowedUrls).permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                                .anyRequest().authenticated()
                )
                .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }

}
