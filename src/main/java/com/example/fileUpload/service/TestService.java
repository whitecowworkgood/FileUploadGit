package com.example.fileUpload.service;

import com.example.fileUpload.message.TestMessage;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.TestVO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TestService {

    void processFiles(FileDto fileDto) throws InterruptedException;

    Object selectStatus(String code);
/*    List<String> doService(FileDto fileDto);

    CompletableFuture<String> AsyncService(FileDto fileDto) throws InterruptedException;

    void enqueueFile(FileDto fileDto);

    CompletableFuture<List<String>> processFiles(FileDto fileDto);*/
}
