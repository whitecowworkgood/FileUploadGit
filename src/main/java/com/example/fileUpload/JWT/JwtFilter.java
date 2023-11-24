package com.example.fileUpload.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String jwt = resolveToken(request);

        if (isSpecialStringCheckToken(jwt)) {
            sendJsonErrorResponse(response, "사용된 토큰이 잘못되었습니다.");
            return;
        }

        if (isValidateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if(isValidateAuthorization(bearerToken)){
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isValidateToken(String jwt){
        return StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt);
    }

    private boolean isSpecialStringCheckToken(String jwt){
        return jwt != null && containsPattern(jwt);
    }

    private boolean isValidateAuthorization(String bearerToken){
        return StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX);
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
