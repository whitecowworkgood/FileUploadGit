package com.example.fileUpload.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.example.fileUpload.util.DirectoryChecker.callGenerateFolderMethods;


@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
public class Aop {
    @Value("${Save-Directory}")
    private String dir;


    private boolean kekGenerated = false; // 초기에는 false로 설정

    /**
     *
     * @author 임재준
     * AOP를 이용하여, service 클래스(컴포넌트) 호출시, 트랜잭션을 감지 및 출력
     *
     * */

    @Around("execution(* com.example.fileUpload.service.*.*(..))")
    public Object serviceLogger(ProceedingJoinPoint joinPoint) throws Throwable{

        try{
            log.info("[서비스 트랜잭션 시작] {}", joinPoint.getSignature());

            Object result = joinPoint.proceed();
            log.info("[서비스 트랜잭션 커밋] {}", joinPoint.getSignature());

            return result;
        }catch (Exception e){
            log.info("[서비스 트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        }finally {
            log.info("[서비스 리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    /**
     *
     * @author 임재준
     * AOP를 이용하여, controller 클래스(컴포넌트) 호출시, 트랜잭션을 감지 및 출력
     * */
    @Around("execution(* com.example.fileUpload.controller.*.*(..))")
    public Object controllerLogger(ProceedingJoinPoint joinPoint) throws Throwable{

        try{
            log.info("[컨트롤러 트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            log.info("[컨트롤러 트랜잭션 커밋] {}", joinPoint.getSignature());

            return result;
        }catch (Exception e){
            log.info("[컨트롤러 트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        }finally {
            log.info("[컨트롤러 리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    @Before("execution(* com.example.fileUpload.*.*.*(..))")
    public void downloadFolderCheck() {

        callGenerateFolderMethods(dir);
    }
}
