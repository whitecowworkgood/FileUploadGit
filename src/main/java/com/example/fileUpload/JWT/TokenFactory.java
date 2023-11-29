package com.example.fileUpload.JWT;

import com.example.fileUpload.model.Token.TokenDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;


public class TokenFactory {

    private final String AUTHORITIES_KEY = "auth";
    private final String SUBJECT_TYPE = "sub";
    private final String BEARER_TYPE = "Bearer";
    private final String JWT_TYPE = "type";
    private final String JWT_VALUE="JWT";
    private final String EXPIRATION_TYPE = "exp";

    private static final long accessTokenExpireTime = 3600000L;
    private static final long refreshTokenExpireTime = 10800000L;


    private TokenFactory() {
    }

    private static class LazyHolder {
        private static final TokenFactory instance = new TokenFactory();
    }

    public static TokenFactory getInstance() {
        return TokenFactory.LazyHolder.instance;
    }

    public TokenDto generateJWTTokenPairOf(Authentication authentication, String SignatureKey){
        return generateJWTTokenPairOf(authentication, SignatureKey,false);
    }

    public TokenDto generateJWTTokenPairOf(Authentication authentication, String SignatureKey, boolean isTokenPair){
        return this.generateTokenDto(authentication, SignatureKey, isTokenPair);
    }

    private TokenDto generateTokenDto(Authentication authentication, String SignatureKey, boolean isTokenPair) {

        Key key = Keys.hmacShaKeyFor(SignatureKey.getBytes());
        long baseTime = (new Date()).getTime();

        String accessToken = generateAccessToken(key, authentication, baseTime);

        TokenDto.TokenDtoBuilder builder = TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .userKey(SignatureKey);

        if (isTokenPair) {
            String refreshToken = generateRefreshToken(key, authentication, baseTime);
            builder.refreshToken(refreshToken);
        }

        return builder.build();
    }

    private String generateAccessToken(Key key, Authentication authentication, long baseTime) {

        String authorities = getAuthoritiesString(authentication);
        Date accessTokenExpiresIn = new Date(baseTime + accessTokenExpireTime);

        return Jwts.builder()
                .setHeaderParam(JWT_TYPE, JWT_VALUE)
                .claim(SUBJECT_TYPE, authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(EXPIRATION_TYPE, accessTokenExpiresIn)
                .signWith(key)
                .compact();
    }

    private String generateRefreshToken(Key key, Authentication authentication, long baseTime) {

        Date refreshTokenExpiresIn = new Date(baseTime + refreshTokenExpireTime);

        return Jwts.builder()
                .setHeaderParam(JWT_TYPE, JWT_VALUE)
                .claim(SUBJECT_TYPE, authentication.getName())
                .claim(EXPIRATION_TYPE, refreshTokenExpiresIn)
                .signWith(key)
                .compact();
    }

    private String getAuthoritiesString(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

}
