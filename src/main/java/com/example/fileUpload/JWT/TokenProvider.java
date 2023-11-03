package com.example.fileUpload.JWT;



import com.example.fileUpload.model.Token.TokenDto;

import com.example.fileUpload.repository.RefreshTokenDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
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

import static com.example.fileUpload.util.FileUtil.generateRandomString;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";

    @Value("${jwt.token-validity-in-seconds}")
    private long accessTokenExpireTime;

    @Value("${refresh-in-seconds}")
    private long refreshTokenExpireTime;

    private final RefreshTokenDao refreshTokenDao;

    public TokenDto generateTokenDto(Authentication authentication, boolean generateRefreshToken) {

        //키를 생성하는 방법은 여기에 두면 안되고, 역할을 분리해야 함
        //우선 사용자 명을 가져오는 방법을 찾지 못해서 여기에 뒀지만, 해결책을 찾고, 코드 수정하기
        String random = generateSigningKey();

        if (!generateRefreshToken) {
            // Refresh Token 생성 시 새로운 키를 생성
            random = refreshTokenDao.selectKey(authentication.getName());
        }

        byte[] keyBytes = Decoders.BASE64.decode(random);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenExpireTime);

        String accessToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .claim("sub", authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim("exp", accessTokenExpiresIn)
                .signWith(key)
                .compact();

        TokenDto.TokenDtoBuilder builder = TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .userKey(random);

        if (generateRefreshToken) {
            // Refresh Token 생성
            String refreshToken = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .claim("sub", authentication.getName())
                    .claim("exp", new Date(now + refreshTokenExpireTime))
                    .signWith(key)
                    .compact();

            builder.refreshToken(refreshToken);
        }

        return builder.build();
    }




    public Authentication getAuthentication(String token) throws JsonProcessingException {
        Key validateKey = parseSigningKey(token);
        // 토큰 복호화
        Claims claims = parseClaims(token, validateKey);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {

            Key validateKey = parseSigningKey(token);

            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(validateKey).build().parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());

        }catch (io.jsonwebtoken.security.SignatureException | SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException | JsonProcessingException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private Key parseSigningKey(String token) throws JsonProcessingException {
        String base64Payload = token.split("\\.")[1];
        byte[] decodedBytes = Base64.decodeBase64(base64Payload);
        String payload = new String(decodedBytes);

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(payload);
        String key = refreshTokenDao.selectKey(jsonNode.get("sub").asText());

        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));

    }

    private String generateSigningKey(){
       return generateRandomString(50);
    }

    private Claims parseClaims(String accessToken, Key key) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();

        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
