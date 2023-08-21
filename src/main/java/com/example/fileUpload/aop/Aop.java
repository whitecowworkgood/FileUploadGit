package com.example.fileUpload.aop;

import com.example.fileUpload.dto.FileDto;
import com.example.fileUpload.entity.FileEntity;
import com.example.fileUpload.repository.SaveFileRepository;
import com.example.fileUpload.service.FileUploadService;
import com.example.fileUpload.unit.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
public class Aop {
    @Value("${Save-Directory}")
    String dir;

    private final SaveFileRepository saveFileRepository;

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

    @Around("execution(* com.example.fileUpload.service.*.*(..))")
    public Object validateDownloadFolderWithDB(ProceedingJoinPoint joinPoint) throws Throwable{
        try {
            log.info("[검증 시작] {}", joinPoint.getSignature());

            //List<FileDto> dbFiles = fileUploadService.printAll();
            List<FileEntity> fileEntities = saveFileRepository.findAll();
            List<String> folderData = FileUtil.getFolderFiles(dir);


            log.info("Folder Files: {}", folderData.toString());

            if (fileEntities.size() != folderData.size()) {
                log.warn("DB와 폴더의 갯수가 틀립니다.");
                // 필요한 처리, 관리자페이지가 있다면 알려준다거나 기타 방법이 있음
            }

            List<String> dbFileNames = fileEntities.stream()
                    .map(FileEntity::getFileName)
                    .collect(Collectors.toList());

            boolean allMatch = folderData.stream().allMatch(dbFileNames::contains);

            if (!allMatch) {
                log.warn("DB와 Folder 데이터가 일치하지 않습니다.");
                // 필요한 처리, 관리자페이지가 있다면 알려준다거나 기타 방법이 있음
                // 자동화...?
            }

            Object result = joinPoint.proceed();
            log.info("[검증 종료] {}", joinPoint.getSignature());

            return result;
        }catch(Exception e){
            ExceptionUtils.getStackTrace(e);
            throw e;
        }
    }


}