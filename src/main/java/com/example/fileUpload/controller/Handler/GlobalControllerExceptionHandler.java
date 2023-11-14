package com.example.fileUpload.controller.Handler;

import com.example.fileUpload.message.ExceptionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionMessage> handleTypeNotValidException(MethodArgumentNotValidException ex){
        ExceptionMessage exceptionMessage = new ExceptionMessage();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            exceptionMessage.setMessage(error.getDefaultMessage());
        });

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
        exceptionMessage.setMessage("자원 선택에 문제가 있습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(exceptionMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionMessage> handleIllegalArgumentException(IllegalArgumentException ex) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage("불법 논변");

        return ResponseEntity.status(HttpStatus.OK).body(exceptionMessage);
    }
    //경로에 빈칸이 있가나 해서, 경로를 찾을 수 없는 경우
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ExceptionMessage> handleMissingPathVariableException(MissingPathVariableException ex) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage("경로가 잘못 지정 되었습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(exceptionMessage);
    }
}
