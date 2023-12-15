
package com.example.fileUpload.service.serviceImpl;
/*
import com.example.fileUpload.message.TestMessage;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.TestVO;
import com.example.fileUpload.repository.TestDAO;
import com.example.fileUpload.service.TestService;
import com.example.fileUpload.util.AsyncClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final AsyncClass asyncClass;
    private final TestDAO testDao;
    @Override
    public void processFiles(FileDto fileDto) {

        testDao.insertTest(fileDto);
        //Thread.sleep(10000);
        Thread extractionThread = new Thread(()->{
            CompletableFuture<Void> future = asyncClass.processFiles(fileDto);

            while (!future.isDone()) {
                try {
                    Thread.sleep(100); // 작업이 완료되지 않았으면 잠시 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        });

        extractionThread.start();

    }

    @Override
    public Object selectStatus(String code) {
        TestVO testVO = testDao.selectStatusCode(code);

        TestMessage testMessage = new TestMessage();
        if(testVO!=null){
            if(testVO.getStatusCode().equals("processing")){
                testMessage.setStatus(testVO.getStatusCode());
            }
            if(testVO.getStatusCode().equals("watting")){
                testMessage.setStatus(testVO.getStatusCode());
            }
            if(testVO.getStatusCode().equals("complete")){
                testMessage.setStatus(testVO.getStatusCode());
                List<String> list = accessOleFolder(testVO.getOlePath());
                testMessage.setFiles(list);

            }
        }
        return testMessage;
    }

    private List<String> accessOleFolder(String olePath) {
        Stream<Path> paths = null;
        try{
            paths = Files.walk(Paths.get(olePath));
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (NoSuchFileException e){
            log.warn("OLE 추출 가능한 파일이 아님, key: "+olePath);
            ExceptionUtils.getStackTrace(e);

        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            log.error("OLE 폴더 접근 에러 발생: ", e);
        } finally {
            paths = null;
        }
        return Collections.singletonList("");
    }
}
*/
