package com.example.fileUpload.util;

import com.example.fileUpload.documentParser.FileProcessor;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.repository.TestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.LinkedBlockingQueue;
@Slf4j
@Component
@RequiredArgsConstructor

public class AsyncClass {
    private final FileProcessor fileProcessor;
    private final TestDao testDao;
    private final BlockingQueue<FileDto> fileQueue = new LinkedBlockingQueue<>();

    @Async
    public CompletableFuture<Void> processFiles(FileDto fileDto) {
        enqueueFile(fileDto);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 큐에서 파일 경로를 가져옴 (대기 상태일 경우 여기서 차단됨)
                FileDto fileDtoFromQueue = fileQueue.take();

                testDao.updateStatusCode(fileDtoFromQueue.getComment());
                //Thread.sleep(1000);
                fileProcessor.createOleExtractorHandlerTest(fileDtoFromQueue);
                return null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CompletionException(e);
            }
        });
    }

    private void enqueueFile(FileDto fileDto) {
        // 큐에 파일 경로 추가
        fileQueue.add(fileDto);
    }
}
