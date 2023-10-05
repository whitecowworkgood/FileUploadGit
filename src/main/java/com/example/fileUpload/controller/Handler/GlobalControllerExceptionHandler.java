package com.example.fileUpload.controller.Handler;

import com.example.fileUpload.message.GetMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GetMessage> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        //log.warn(ex.getMessage());
        GetMessage getMessage = new GetMessage();
        return ResponseEntity.status(HttpStatus.OK).body(getMessage);
    }


}
