package com.example.fileUpload.Security;

import com.example.fileUpload.Security.Wrapper.RequestWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenDtoFilter implements Filter {

    private final RequestMatcher[] requiresXssProtectionRequestMatchers;
    private final String base64ErrorPattern = "[\\+/=,:;\"&<>^']"; //추가적으로 더 있을것임, 추가하기

    public TokenDtoFilter(String... patterns) {
        requiresXssProtectionRequestMatchers = new RequestMatcher[patterns.length];

        for (int i = 0; i < patterns.length; i++) {
            requiresXssProtectionRequestMatchers[i] = new AntPathRequestMatcher(patterns[i]);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        boolean shouldChain = false;
        //TokenDtoWrapper tokenDtoWrapper = null;
        RequestWrapper requestWrapper = null;
        ServletInputStream servletInputStream = null;

        for (RequestMatcher requestMatcher : requiresXssProtectionRequestMatchers) {

            if (requestMatcher.matches((HttpServletRequest) request)) {

                if (((HttpServletRequest) request).getMethod().equals(HttpMethod.POST.toString()) &&
                        request.getContentType() != null &&
                        request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {



                    //tokenDtoWrapper = new TokenDtoWrapper((HttpServletRequest) request);
                    requestWrapper = new RequestWrapper((HttpServletRequest) request);

                    ObjectMapper objectMapper = new ObjectMapper();

                    try {
                        //servletInputStream = tokenDtoWrapper.getInputStream();
                        servletInputStream = requestWrapper.getInputStream();
                        JsonNode jsonNode = objectMapper.readTree(servletInputStream);

                        String accessToken = jsonNode.get("accessToken").asText();
                        String refreshToken = jsonNode.get("refreshToken").asText();

                        if (isSpecialStringCheckToken(accessToken) || isSpecialStringCheckToken(refreshToken)) {
                            sendJsonErrorResponse( (HttpServletResponse) response, "잘못된 값을 입력하였습니다.");
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

            //chain.doFilter(tokenDtoWrapper, response);
            chain.doFilter(requestWrapper, response);
        } else {

            chain.doFilter(request, response);
        }
    }

    private boolean isSpecialStringCheckToken(String jwt){
        return jwt != null && containsPattern(jwt);
    }


    private boolean containsPattern(String tokenString){
        Pattern regex = Pattern.compile(base64ErrorPattern);
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
