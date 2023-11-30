package com.example.fileUpload.aop;

import com.example.fileUpload.util.DirectoryChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class Aop {

    @Value("${Ole-Directory}")
    private String oleDir;
    @Value("${Temp-Diractory}")
    private String tempDir;
    @Value("${Download-Directory}")
    private String downloadDir;


    @Around("execution(* com.example.fileUpload.service.*.*(..))")
    public Object serviceLogger(ProceedingJoinPoint joinPoint) throws Throwable{

        try{
            log.info("[서비스 트랜잭션 시작] {}", joinPoint.getSignature());
            long startTime = System.nanoTime();
            Object result = joinPoint.proceed();
            log.info("[서비스 트랜잭션 커밋] {}", joinPoint.getSignature());

            long endTime = System.nanoTime();
            double elapsedTimeInMilliseconds = (endTime - startTime)/1e6;

            log.info(String.format("Service Processing Time: %.2f milliseconds", elapsedTimeInMilliseconds));

            return result;
        }catch (Exception e){
            log.info("[서비스 트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        }finally {
            log.info("[서비스 리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    @Around("execution(* com.example.fileUpload.controller.*.*(..))")
    public Object controllerLogger(ProceedingJoinPoint joinPoint) throws Throwable{

        try{

            log.info("[컨트롤러 트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            long startTime = System.nanoTime();
            log.info("[컨트롤러 트랜잭션 커밋] {}", joinPoint.getSignature());


            long endTime = System.nanoTime();
            double elapsedTimeInMilliseconds = (endTime - startTime)/1e6;

            log.info(String.format("Controller Processing Time: %.2f milliseconds", elapsedTimeInMilliseconds));

            return result;
        }catch (Exception e){
            log.info("[컨트롤러 트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        }finally {
            log.info("[컨트롤러 리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    @Before("execution(* com.example.fileUpload.*.*(..))")
    public void downloadFolderCheck() {
        DirectoryChecker.generateFolder(oleDir);
        DirectoryChecker.generateFolder(tempDir);
        DirectoryChecker.generateFolder(downloadDir);
    }

}
