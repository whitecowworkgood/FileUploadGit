package com.example.fileUpload.JWT;

import com.example.fileUpload.repository.RefreshTokenDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidate {

    private final RefreshTokenDAO refreshTokenDao;
    private final String AUTHORITIES_KEY = "auth";
    private final String SUBJECT_TYPE = "sub";


    public Authentication getAuthentication(String token) {
        try{
            Key validateKey = parseSignitureKey(token);
            Claims claims = parseClaims(token, validateKey);

            if (claims.get(AUTHORITIES_KEY) == null) {
                throw new RuntimeException("권한 정보가 없는 토큰입니다.");
            }

            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            UserDetails principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, "", authorities);

        }catch (NullPointerException e){
            throw new RuntimeException("로그아웃된 사용자 입니다.");

        }catch (JsonProcessingException e){
            throw new RuntimeException("토큰에서 값을 가져오는데 실패하였습니다.");
        }catch (ExpiredJwtException e){
            throw new RuntimeException("JWT 토큰의 사용기간이 만료되었습니다.");
        }

    }

    public boolean ValidateExpiration(String token) throws NullPointerException, JsonProcessingException, ExpiredJwtException {

        Key validateKey = parseSignitureKey(token);
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(validateKey).build().parseClaimsJws(token);

        return claims.getBody().getExpiration().before(new Date());

    }
    private Key parseSignitureKey(String token) throws NullPointerException, JsonProcessingException, ExpiredJwtException{

        String userName = decodeTokenPayload(token);
        String key = refreshTokenDao.selectKey(userName);

        return Keys.hmacShaKeyFor(key.getBytes());
    }

    private String decodeTokenPayload(String token) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        String base64Payload = token.split("\\.")[1];
        byte[] decodedBytes = Base64.decodeBase64(base64Payload);
        String payload = new String(decodedBytes);

        return objectMapper.readTree(payload).get(SUBJECT_TYPE).asText();
    }
    private Claims parseClaims(String accessToken, Key key) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();

        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
