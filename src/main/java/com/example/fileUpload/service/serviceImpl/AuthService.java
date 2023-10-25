package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.JWT.TokenProvider;
import com.example.fileUpload.Security.SecurityUtil;
import com.example.fileUpload.model.Member.Member;
import com.example.fileUpload.model.Member.MemberRequestDto;
import com.example.fileUpload.model.Member.MemberResponseDto;
import com.example.fileUpload.model.Token.RefreshToken;
import com.example.fileUpload.model.Token.TokenDto;
import com.example.fileUpload.model.Token.TokenRequestDto;
import com.example.fileUpload.repository.MemberDao;
import com.example.fileUpload.repository.RefreshTokenDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenDao refreshTokenDao;

    @Transactional
    public MemberResponseDto signup(MemberRequestDto memberRequestDto){
        if(memberDao.existsByAccount(memberRequestDto.getAccount())){
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        Member member = memberRequestDto.toMember(passwordEncoder);

        member.setAccount(HtmlUtils.htmlEscape(member.getAccount()));

        return MemberResponseDto.of(memberDao.save(member));
    }

    @Transactional
    public TokenDto login(MemberRequestDto memberRequestDto){

        memberRequestDto.setAccount(HtmlUtils.htmlEscape(memberRequestDto.getAccount()));

        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();
        log.info(memberRequestDto.getAccount());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);



        if(refreshTokenDao.existsByAccount(authentication.getName())){
            throw new RuntimeException("이미 로그인이 되어있는 사용자입니다.");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication, true);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenDao.save(refreshToken);

        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto){

        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        if(!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())){
            refreshTokenDao.removeRefreshTokenByValue(authentication.getName());
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }


        RefreshToken refreshToken = refreshTokenDao.findByKey(authentication.getName())
                .orElseThrow(()->new RuntimeException("로그아웃된 사용자 입니다."));

        if(!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())){
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }
        //tokenProvider.getRefreshTokenExpiration(tokenRequestDto.getRefreshToken());
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication, false);

        return tokenDto;
    }

    @Transactional
    public String logout(TokenRequestDto tokenRequestDto){

        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        if(!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())){
            refreshTokenDao.removeRefreshTokenByValue(authentication.getName());
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        RefreshToken refreshToken = refreshTokenDao.findByKey(authentication.getName())
                .orElseThrow(()->new RuntimeException("로그아웃된 사용자 입니다."));

        if(!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())){
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        if (!refreshTokenDao.removeRefreshTokenByValue(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("로그아웃에 실패하였습니다.");
        }
        return "logout";

    }
    @Transactional(readOnly = true)
    public String getUserNameWeb() {
        return SecurityUtil.getCurrentUsername().get();
    }
}
