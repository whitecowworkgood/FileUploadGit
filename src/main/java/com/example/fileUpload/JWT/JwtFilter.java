package com.example.fileUpload.JWT;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private static final String BASE64_ERROR_PATTERN = "[\\+/=,:;\"&<>^']"; //추가적으로 더 있을것임, 추가하기


    private final TokenValidate tokenValidate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String jwt = extractJwtTokenFromHeader(request);

        if (isSpecialStringCheckToken(jwt)) {
            sendJsonErrorResponse(response, "사용된 토큰이 잘못되었습니다.");
            return;
        }

        if (isValidateToken(response, jwt)) {
            Authentication authentication = tokenValidate.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidateToken( HttpServletResponse response, String jwt) throws IOException {
        boolean validateValue = false;
        try{
            validateValue =  StringUtils.hasText(jwt) && !tokenValidate.ValidateExpiration(jwt);

        } catch (io.jsonwebtoken.security.SignatureException | SecurityException | MalformedJwtException e) {
            sendJsonErrorResponse(response, "잘못된 JWT 토큰입니다.");

        } catch (ExpiredJwtException e) {
            sendJsonErrorResponse(response, "만료된 JWT 토큰입니다.");

        } catch (UnsupportedJwtException e) {
            sendJsonErrorResponse(response, "지원되지 않는 JWT 토큰입니다.");

        } catch (IllegalArgumentException e) {
            sendJsonErrorResponse(response, "JWT 토큰이 잘못되었습니다.");

        } catch(NullPointerException e){
            sendJsonErrorResponse(response, "로그아웃된 사용자 접근");
        }
        return validateValue;
    }
    private boolean isSpecialStringCheckToken(String jwt){
        return StringUtils.hasText(jwt) && containsPattern(jwt);
    }
    private boolean isValidateBearerPrefix(String bearerToken){
        return StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX);
    }
    private String extractJwtTokenFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if(isValidateBearerPrefix(bearerToken)){
            return bearerToken.substring(7);
        }

        return "";
    }
    private boolean containsPattern(String tokenString){
        Pattern regex = Pattern.compile(BASE64_ERROR_PATTERN);
        Matcher matcher = regex.matcher(tokenString);
        return matcher.find();
    }
    private void sendJsonErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        try (PrintWriter writer = response.getWriter()) {
            String jsonMessage = "{\"statusCode\": \"401\", \"message\": \"" + errorMessage + "\"}";
            writer.write(jsonMessage);
        }
    }
}
