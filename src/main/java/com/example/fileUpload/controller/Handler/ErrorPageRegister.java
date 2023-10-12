package com.example.fileUpload.controller.Handler;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorPageRegister implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage forbiddenErrorPage = new ErrorPage(HttpStatus.FORBIDDEN, "/index.html"); // FORBIDDEN 상태 코드
        ErrorPage notFoundErrorPage = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html"); // NOT FOUND 상태 코드
        registry.addErrorPages(forbiddenErrorPage, notFoundErrorPage);
    }


}