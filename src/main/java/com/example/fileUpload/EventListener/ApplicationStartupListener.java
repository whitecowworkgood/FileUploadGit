package com.example.fileUpload.EventListener;

import com.example.fileUpload.service.FileEncryptService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final FileEncryptService fileEncryptService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        fileEncryptService.createRSAKeyPair();

    }
}
