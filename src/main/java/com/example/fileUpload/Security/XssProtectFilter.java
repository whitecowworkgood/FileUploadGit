package com.example.fileUpload.Security;

import com.example.fileUpload.Security.Wrapper.XssEscapeRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

public class XssProtectFilter implements Filter {
    private final RequestMatcher requiresXssProtectionRequestMatcher;

    public XssProtectFilter(String pattern) {
        requiresXssProtectionRequestMatcher = new AntPathRequestMatcher(pattern);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (requiresXssProtectionRequestMatcher.matches((HttpServletRequest) request)) {

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
}