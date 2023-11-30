package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.JWT.TokenFactory;
import com.example.fileUpload.JWT.TokenValidate;
import com.example.fileUpload.model.Member.Member;
import com.example.fileUpload.model.Member.MemberRequestDto;
import com.example.fileUpload.model.Member.MemberResponseDto;
import com.example.fileUpload.model.Token.RefreshToken;
import com.example.fileUpload.model.Token.TokenDto;
import com.example.fileUpload.model.Token.TokenRequestDto;
import com.example.fileUpload.repository.MemberDao;
import com.example.fileUpload.repository.RefreshTokenDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;
    private final TokenValidate tokenValidate;
    private final RefreshTokenDao refreshTokenDao;

    @Transactional
    public MemberResponseDto signup(MemberRequestDto memberRequestDto){

        if(memberDao.existsByAccount(memberRequestDto.getAccount())){
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        Member member = memberRequestDto.toMember(passwordEncoder);
        return MemberResponseDto.of(memberDao.save(member));
    }

    @Transactional
    public TokenDto login(MemberRequestDto memberRequestDto){

        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        if(isLoginAccount(authentication.getName())){
            throw new RuntimeException("이미 로그인이 되어있는 사용자입니다.");
        }

        return createAndSaveTokenDto(authentication);
    }

    private boolean isLoginAccount(String userName){
        return refreshTokenDao.existsByAccount(userName);
    }

    private TokenDto createAndSaveTokenDto(Authentication authentication){
        String signatureKey = generateRandomString(74);

        TokenDto tokenDto = TokenFactory.getInstance()
                .generateJWTTokenPairOf(authentication, signatureKey, true);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .signKey(tokenDto.getUserKey())
                .build();

        Executors.newCachedThreadPool().execute(()->{
            refreshTokenDao.save(refreshToken);
        });

        return tokenDto;
    }


    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) throws JsonProcessingException {

        try {
            tokenValidate.ValidateExpiration(tokenRequestDto.getRefreshToken());

            Authentication authentication = tokenValidate.getAuthentication(tokenRequestDto.getAccessToken());

            RefreshToken refreshToken = refreshTokenDao.findByKey(authentication.getName())
                    .orElseThrow(()->new RuntimeException("로그아웃된 사용자 입니다."));

            if(!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())){
                throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
            }

            String signatureKey = refreshTokenDao.selectKey(authentication.getName());

            return TokenFactory.getInstance()
                    .generateJWTTokenPairOf(authentication,signatureKey);

        }catch (io.jsonwebtoken.security.SignatureException | SecurityException | MalformedJwtException e) {
            throw new RuntimeException("잘못된 JWT 토큰입니다.");

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("만료된 JWT 토큰입니다.");

        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("지원되지 않는 JWT 토큰입니다.");

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT 토큰이 잘못되었습니다.");

        } catch(NullPointerException e){
            throw new RuntimeException("로그아웃된 사용자 접근");
        }
    }

    @Transactional
    public String logout(TokenRequestDto tokenRequestDto) {
        try {
            Authentication authentication = tokenValidate.getAuthentication(tokenRequestDto.getAccessToken());

            if (tokenValidate.ValidateExpiration(tokenRequestDto.getRefreshToken())) {
                throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
            }

            RefreshToken refreshToken = refreshTokenDao.findByKey(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("로그아웃된 사용자 입니다."));

            if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
                throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
            }

            if (!refreshTokenDao.removeRefreshTokenByValue(tokenRequestDto.getRefreshToken())) {
                throw new RuntimeException("로그아웃에 실패하였습니다.");
            }

            return "로그아웃을 성공적으로 수행하였습니다.";

        } catch (IllegalArgumentException | JsonProcessingException | io.jsonwebtoken.security.SignatureException e) {
            throw new RuntimeException("검증에 실패하였습니다.");
        }
    }
    @Transactional(readOnly = true)
    public String getUserNameWeb() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        return currentUser.getName();
    }

    public String generateRandomString(int length) {

        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(index);
            stringBuffer.append(randomChar);
        }

        return stringBuffer.toString();
    }
}
