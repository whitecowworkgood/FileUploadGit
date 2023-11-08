package com.example.fileUpload.Security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.fileUpload.util.scriptRegex.scriptPatterns;

@Slf4j
public class MultipartUploadFilter implements Filter {
    private final RequestMatcher requiresXssProtectionRequestMatcher;

    public MultipartUploadFilter(String pattern) {
        requiresXssProtectionRequestMatcher = new AntPathRequestMatcher(pattern);

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (requiresXssProtectionRequestMatcher.matches((HttpServletRequest) request)) {
            if (isMultiPartType(request)) {
                if (isValidFileName(request)) {
                    chain.doFilter(request, response);

                } else {
                    sendMultiPartErrorResponse((HttpServletResponse) response, "파일명이 잘못되었습니다.");
                }
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isMultiPartType(ServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith("multipart/form-data");
    }

    private boolean isValidFileName(ServletRequest request) throws IOException, ServletException {
        String fileName = ((HttpServletRequest) request).getPart("file").getSubmittedFileName();
        for (Pattern pattern : scriptPatterns) {
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find()) {
                return false;
            }
        }
        return true;
    }

    private void sendMultiPartErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        try (PrintWriter writer = response.getWriter()) {
            String jsonMessage = "{\"statusCode\": \"401\", \"message\": \"" + errorMessage + "\"}";
            writer.write(jsonMessage);
            writer.flush();
        }
    }
}
