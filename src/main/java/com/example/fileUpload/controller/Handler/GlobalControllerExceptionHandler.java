package com.example.fileUpload.controller.Handler;

import com.example.fileUpload.message.ExceptionMessage;
import com.example.fileUpload.message.GetMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessage> handleException(Exception ex) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();

        exceptionMessage.setMessage("응애 나 최상위 예외");

        return ResponseEntity.status(HttpStatus.OK).body(exceptionMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionMessage> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {

        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage("올바르지 않는 타입의 매개변수 입니다.");

        return ResponseEntity.status(HttpStatus.OK).body(exceptionMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionMessage> handleRuntimeException(RuntimeException ex) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.OK).body(exceptionMessage);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionMessage> handleRuntimeException(NoHandlerFoundException ex) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage("해당 경로를 찾을 수 없습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(exceptionMessage);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ExceptionMessage> handleJsonProcessingException(IllegalAccessException ex) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage(ex.getCause().getMessage());

        return ResponseEntity.status(HttpStatus.OK).body(exceptionMessage);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ExceptionMessage> handleNullPointException(NullPointerException ex) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage("선택된 파일을 찾을 수 없습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(exceptionMessage);
    }

}
