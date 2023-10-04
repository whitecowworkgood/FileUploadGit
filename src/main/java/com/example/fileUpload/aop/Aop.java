package com.example.fileUpload.aop;

import com.example.fileUpload.service.FileEncryptService;
import com.example.fileUpload.service.serviceImpl.FileEncryptServiceImpl;
import jdk.swing.interop.SwingInterOpUtils;
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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
public class Aop {
    @Value("${Save-Directory}")
    String dir;

    private final FileEncryptService fileEncryptService;

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

    /***
     *
     * 서비스 클래스(컴포넌트) 수행 전에, 다운로드 폴더가 있는지 확인 후, 없으면 생성
     * @throws Throwable
     */
    @Before("execution(* com.example.fileUpload.controller.*.*(..))")
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

    @Before("execution(* com.example.fileUpload.*.*(..))")
    public void generateKEK() throws NoSuchAlgorithmException, SocketException, UnknownHostException {
        if (!kekGenerated) {
            fileEncryptService.getMacAddress();
            fileEncryptService.generateKEK();
            kekGenerated = true;
        }

    }

    @Before("execution(* com.example.fileUpload.service.FileUploadService.fileUpload(..))")
    public void RSA() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        fileEncryptService.storedRSAKeyPair();
    }

    @After("execution(* com.example.fileUpload.service.FileEncryptService.encryptFile(..))")
    public void test(){
        System.out.println("암호화 완료");
    }
}
