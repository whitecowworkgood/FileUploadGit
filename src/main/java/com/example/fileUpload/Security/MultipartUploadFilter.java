package com.example.fileUpload.Security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                String fileName = ((HttpServletRequest) request).getPart("file").getSubmittedFileName();
                boolean isValidFileName = true;

                for (Pattern pattern : scriptPatterns) {
                    Matcher matcher = pattern.matcher(fileName);
                    if (matcher.find()) {
                        isValidFileName = false;
                        break;
                    }
                }

                if (!isValidFileName) {
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.setContentType("application/json");
                    httpResponse.setCharacterEncoding("UTF-8");
                    httpResponse.setStatus(HttpServletResponse.SC_OK);

                    try (PrintWriter writer = httpResponse.getWriter()) {
                        String jsonMessage = "{\"statusCode\": \"401\", \"message\": \"" + "파일명이 잘못되었습니다." + "\"}";
                        writer.write(jsonMessage);
                    }
                } else {
                    chain.doFilter(request, response);
                }
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
        //chain.doFilter(request, response);
    }

}
