package com.example.fileUpload;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.example.fileUpload.Mybatis")
@Import(AsyncConfig.class)
public class FileUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileUploadApplication.class, args);
    }

}
