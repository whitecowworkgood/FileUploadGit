package com.example.fileUpload.Security;

import com.example.fileUpload.Security.Wrapper.MemberRequestDtoWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class MemberRequestDtoFilter implements Filter {
    private final RequestMatcher[] requiresXssProtectionRequestMatchers;

    public MemberRequestDtoFilter(String... patterns) {
        requiresXssProtectionRequestMatchers = new RequestMatcher[patterns.length];

        for (int i = 0; i < patterns.length; i++) {
            requiresXssProtectionRequestMatchers[i] = new AntPathRequestMatcher(patterns[i]);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        boolean shouldChain = false;
        MemberRequestDtoWrapper memberRequestDtoWrapper = null;
        ServletInputStream servletInputStream = null;

        for (RequestMatcher requestMatcher : requiresXssProtectionRequestMatchers) {

            if (requestMatcher.matches((HttpServletRequest) request)) {

                if (((HttpServletRequest) request).getMethod().equals(HttpMethod.POST.toString()) &&
                        request.getContentType() != null &&
                        request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {



                    memberRequestDtoWrapper = new MemberRequestDtoWrapper((HttpServletRequest) request);

                    ObjectMapper objectMapper = new ObjectMapper();

                    try {
                        servletInputStream = memberRequestDtoWrapper.getInputStream();
                        JsonNode jsonNode = objectMapper.readTree(servletInputStream);

                        String account = jsonNode.get("account").asText();
                        String password = jsonNode.get("password").asText();

                        if (!isValidAccount(account) || !isValidPassword(password)) {
                            sendJsonErrorResponse( (HttpServletResponse) response, "잘못된 값을 입력하였습니다.");
                            return;

                        }
                        if (containsNewlineCharacters(account) || containsNewlineCharacters(password)) {
                            sendJsonErrorResponse((HttpServletResponse) response, "개행문자는 입력할 수 없습니다.");
                            return;
                        }
                        if (!isValidAccountLength(account) || !isValidPasswordLength(password)) {
                            sendJsonErrorResponse((HttpServletResponse) response, "아이디 또는 패스워드의 길이가 적절하지 않습니다. 5~15 글자로 맞춰주세요");
                            return;
                        }

                    } catch (RuntimeException e) {

                        sendJsonErrorResponse((HttpServletResponse) response, e.getMessage());
                        return;
                    }

                    shouldChain = true;
                    break;
                }
            }
        }

        if (shouldChain) {

            chain.doFilter(memberRequestDtoWrapper, response);
        } else {

            chain.doFilter(request, response);
        }
    }


    private boolean isValidAccount(String account) {
        return account.matches("[\\w\\s]*");
    }

    private boolean isValidPassword(String password) {
        return password.matches("[\\w\\s!@#$%^&*()_+-=]*");
    }

    private boolean containsNewlineCharacters(String text) {
        return text.contains("\r") || text.contains("\n") || text.contains("\t");
    }

    private boolean isValidAccountLength(String account) {
        return account.length() >= 5 && account.length() <= 15;
    }

    private boolean isValidPasswordLength(String password) {
        return password.length() >= 5 && password.length() <= 25;
    }
    // JSON 형식의 오류 응답을 전송하는 메서드
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
