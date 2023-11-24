package com.example.fileUpload.controller;


import com.example.fileUpload.message.ResultMessage;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.service.FileUploadService;
import com.example.fileUpload.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/upload")
@Tag(name = "FileUpload", description = "파일 업로드 API 구성")
public class FileUploadController {

    @Value("${Save-Directory}")
    private String baseDir;
    private final FileUploadService fileUploadService;

    @Operation(summary = "파일 업로드", description = "파일을 저장 합니다.")
    @PostMapping("")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "countNum") Long countNum,
                                                        @RequestParam(value = "comment", required = false, defaultValue = "") String comment,
                                                        @RequestParam("file") MultipartFile file) {


        if(countNum<=0 || countNum>10){
            throw new RuntimeException("다운로드 횟수는 0 미만x, 10회까지 허용이 가능합니다.");
        }

        String uuidName = UUID.randomUUID().toString();
        String uuidFileName = generateUuidFileName(file, uuidName);
        String fileSavePath = generateFileSavePath(uuidFileName);
        String fileOlePath = generateFileOlePath(uuidName);
        String dividedComment = dividedCommentSize(comment);

        FileDto fileDto = createFileDto(file, uuidFileName, fileSavePath, fileOlePath, countNum, dividedComment);

        fileUploadService.fileUpload(fileDto);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResultMessage.getInstance().fileUploadOf("File Upload Success"));
    }

    private String generateUuidFileName(MultipartFile file, String uuidName) {
        return new StringBuffer(uuidName)
                .append(FileUtil.getFileExtension(file)).toString();
    }

    private String generateFileSavePath(String uuidFileName) {
        return new StringBuffer(this.baseDir)
                .append(File.separator)
                .append(uuidFileName).toString();
    }

    private String generateFileOlePath(String uuidName) {
        return new StringBuffer(this.baseDir)
                .append(File.separator)
                .append("ole")
                .append(File.separator)
                .append(uuidName)
                .append(File.separator).toString();
    }

    private String dividedCommentSize(String comment){
        int maxLength = 100;
        if(comment != null && comment.length() > maxLength){
            comment = comment.substring(0, maxLength);
        }
        return comment;
    }

    private FileDto createFileDto(MultipartFile file, String uuidFileName, String fileSavePath, String fileOlePath, Long countNum, String comment) {
        return FileDto.builder()
                .UUIDFileName(uuidFileName)
                .originFileName(Objects.requireNonNull(file.getOriginalFilename()))
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileSavePath(fileSavePath)
                .fileOlePath(fileOlePath)
                .countNum(countNum)
                .fileData(file)
                .comment(comment)
                .build();
    }

}
