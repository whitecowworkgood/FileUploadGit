package com.example.fileUpload.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*@Component
public class SpecialCharacterFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String parameter1 = request.getParameter("comment");
        //System.out.println(parameter1);

        if(containsSpecialCharacters(parameter1)){
            throw new RuntimeException("특수문자가 포함되어 있습니다.");
        }

        filterChain.doFilter(request, response);
    }

    private boolean containsSpecialCharacters(String input){
        String specialCharacters = "<>/()&\"'#";
        for(char c : specialCharacters.toCharArray()){
            if(input.contains(String.valueOf(c))){
                return true;
            }
        }
        return false;

    }
}*/
