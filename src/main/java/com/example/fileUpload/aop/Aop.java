package com.example.fileUpload.aop;

import com.example.fileUpload.entity.FileEntity;
import com.example.fileUpload.repository.SaveFileRepository;
import com.example.fileUpload.unit.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;


@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
public class Aop {
    @Value("${Save-Directory}")
    String dir;

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
    @Before("execution(* com.example.fileUpload.service.*.*(..))")
    public void downloadFolderCheck() throws Throwable{
        Path folder = Paths.get(dir);

        // 폴더 존재 여부 확인
        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            try {
                Files.createDirectory(folder);
                System.out.println("Folder created: " + dir);
            } catch (IOException e) {
                // 폴더 생성 실패에 대한 예외 처리
                log.error(ExceptionUtils.getStackTrace(e));
                throw e;
            }
        }
    }
}
