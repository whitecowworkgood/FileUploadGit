package com.example.fileUpload.controller;


import com.example.fileUpload.message.ResultMessage;
import com.example.fileUpload.model.File.UserFileVO;
import com.example.fileUpload.service.FileDownloadService;
import com.example.fileUpload.service.serviceImpl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/download")
@Tag(name = "FileDownload", description = "파일 다운로드 API 구성")
public class FileDownloadController {

    private final FileDownloadService fileDownloadService;
    private final AuthService authService;

    @Operation(summary = "허가받은 파일 출력", description = "관리자로 부터 다운로드 허가 받은 파일을 출력합니다.")
    @GetMapping("/files")
    @ResponseBody
    public ResponseEntity<String> showFiles() {

        String userName = authService.getUserNameWeb();

        List<UserFileVO> userFileVOS = this.fileDownloadService.showAcceptedFiles(userName);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResultMessage.getInstance().userFileOf("User_File_List", userFileVOS));

    }

    @Operation(summary = "선택 파일 다운로드", description = "파일 id를 통해 파일을 다운로드 합니다.")
    @GetMapping("/file")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam("id") Long id) throws IOException {

        if (id <= 0) {
            throw new RuntimeException("잘못된 매개변수를 입력하셨습니다. id값은 0보다 커야 합니다.");
        }

        Resource resource = fileDownloadService.downloadFile(id);

        if (canDownloadFile(id, resource)) {
            fileDownloadService.decreaseCountNum(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodeFileName(id))
                    .contentLength(resource.contentLength())
                    .body(resource);
        }

        return ResponseEntity.ok().build();
    }

    private boolean canDownloadFile(Long id, Resource resource) {
        boolean downloadAble = fileDownloadService.isDownloadAble(id);
        return resource != null && resource.isFile() && downloadAble;
    }

    private String encodeFileName(Long id) {
        String fileName = fileDownloadService.getFileName(id);
        return new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

}
