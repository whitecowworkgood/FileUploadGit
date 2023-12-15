package com.example.fileUpload.controller;


/*
import com.example.fileUpload.message.TestMessage;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.FileExtractionRequest;
import com.example.fileUpload.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/file")
@Tag(name = "File", description = "테스트용 컨트롤러")
public class FileController {

    private final TestService testService;

    @PostMapping("")
    public ResponseEntity<TestMessage> extractFile(@RequestBody FileExtractionRequest fileExtractionRequest) throws IOException, InterruptedException {
    TestMessage testMessage = new TestMessage();

        String mimeType = Files.probeContentType(java.nio.file.Path.of(fileExtractionRequest.getFilePath()));
        String uuid = UUID.randomUUID().toString();

        FileDto fileDto = FileDto.builder()
                .fileSavePath(fileExtractionRequest.getFilePath())
                //.fileType(mimeType)
                .fileOlePath("C:/temp/" + uuid + File.separator)
                .originFileName("test.test")
                .comment(uuid)
                .build();

        testMessage.setFiles(Collections.singletonList(uuid));

        testService.processFiles(fileDto);

        return ResponseEntity.status(HttpStatus.OK).body(testMessage);
    }

    @GetMapping("")
    public ResponseEntity<TestMessage> selectStatus(@RequestParam String code){

        return ResponseEntity.status(HttpStatus.OK).body((TestMessage) testService.selectStatus(code));
    }
}
*/
