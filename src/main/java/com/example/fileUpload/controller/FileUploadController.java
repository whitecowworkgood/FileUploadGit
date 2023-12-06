package com.example.fileUpload.controller;


import com.example.fileUpload.model.DTOFactory.FileDTOFactoryImpl;
import com.example.fileUpload.message.ResultMessage;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/upload")
@Tag(name = "FileUpload", description = "파일 업로드 API 구성")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final FileDTOFactoryImpl dtoFactory;

    @Operation(summary = "파일 업로드", description = "파일을 저장 합니다.")
    @PostMapping("")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "countNum") Long countNum,
                                             @RequestParam(value = "comment", required = false, defaultValue = "") String comment,
                                             @RequestParam("file") MultipartFile file) throws IOException {


        if(countNum<=0 || countNum>10){
            throw new RuntimeException("다운로드 횟수는 0 미만x, 10회까지 허용이 가능합니다.");
        }


        FileDto fileDto = dtoFactory.generateUploadDtoOf(file, countNum, comment);
        fileUploadService.fileUpload(fileDto);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResultMessage.getInstance().fileUploadOf("File Upload Success"));
    }

}
