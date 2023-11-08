package com.example.fileUpload.controller;


import com.example.fileUpload.message.PostDeleteMessage;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.service.FileUploadService;
import com.example.fileUpload.service.serviceImpl.AuthService;
import com.example.fileUpload.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

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
    private final AuthService authService;

    /**
     * 파일을 업로드합니다.
     *
     * @param file 업로드할 파일입니다.
     * @return ResponseEntity<PostDeleteMessage> 파일 업로드 결과를 반환합니다.
     */
    @Operation(summary = "파일 업로드", description = "파일을 저장 합니다.")
    @PostMapping("")
    public ResponseEntity<PostDeleteMessage> uploadFile(@RequestParam("countNum") Long countNum,
                                                        @RequestParam(value = "comment", required = false, defaultValue = "null") String comment,
                                                        @RequestParam("file") MultipartFile file) {
        String userName = authService.getUserNameWeb();
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();

        if(countNum<=0 || countNum>10){
            throw new RuntimeException("다운로드 횟수는 0 미만x, 10회까지 허용이 가능합니다.");
        }

        try {

            String uuidName = UUID.randomUUID().toString();

            String uuidFileName = generateUuidFileName(file, uuidName);
            String fileSavePath = generateFileSavePath(uuidFileName);
            String fileOlePath = generateFileOlePath(uuidName);
            String dividedComment = diviedCommentSize(comment);

            FileDto fileDto = createFileDto(file, uuidFileName, fileSavePath, fileOlePath, countNum, userName, dividedComment);

            fileUploadService.fileUpload(fileDto);

        } catch (Exception e) {
            ExceptionUtils.getStackTrace(e);
            postDeleteMessage.setMessage(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(postDeleteMessage);
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

    private String diviedCommentSize(String comment){
        int maxLength = 100;
        if(comment != null && comment.length() > maxLength){
            comment = comment.substring(0, maxLength);
        }
        return comment;
    }

    private FileDto createFileDto(MultipartFile file, String uuidFileName, String fileSavePath, String fileOlePath, Long countNum, String userName, String comment/*, boolean encryption*/) {
        return FileDto.builder()
                .UUIDFileName(uuidFileName)
                .originFileName(Objects.requireNonNull(file.getOriginalFilename()))
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileSavePath(fileSavePath)
                .fileOlePath(fileOlePath)
                .countNum(countNum)
                .fileData(file)
                .userName(userName)
                .comment(comment)
                .build();
    }

}
