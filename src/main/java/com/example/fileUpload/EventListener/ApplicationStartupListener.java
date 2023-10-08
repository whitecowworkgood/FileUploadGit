package com.example.fileUpload.EventListener;

import com.example.fileUpload.service.FileEncryptService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {



    private final FileEncryptService fileEncryptService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        fileEncryptService.createRSAKeyPair();

    }


}
