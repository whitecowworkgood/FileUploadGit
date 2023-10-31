package com.example.fileUpload.Security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.regex.Pattern;

public class XssProtectFilter implements Filter {
    private final RequestMatcher requiresXssProtectionRequestMatcher;

    public XssProtectFilter(String pattern) {
        requiresXssProtectionRequestMatcher = new AntPathRequestMatcher(pattern);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (requiresXssProtectionRequestMatcher.matches((HttpServletRequest) request)) {
            // 사용자 입력을 검증하고 치환하는 로직을 적용
            if (request instanceof XssEscapeRequestWrapper) {
                chain.doFilter(request, response);
            } else {
                XssEscapeRequestWrapper xssRequestWrapper = new XssEscapeRequestWrapper((HttpServletRequest) request);
                chain.doFilter(xssRequestWrapper, response);
            }
        } else {
            chain.doFilter(request, response);
        }

    }
    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

}