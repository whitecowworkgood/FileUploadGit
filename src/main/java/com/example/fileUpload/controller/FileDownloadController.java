package com.example.fileUpload.controller;


import com.example.fileUpload.message.GetMessage;
import com.example.fileUpload.model.File.UserFileVO;
import com.example.fileUpload.service.FileDownloadService;
import com.example.fileUpload.service.serviceImpl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

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
    public ResponseEntity<GetMessage> showFiles() {

        String userName = authService.getUserNameWeb();

        List<UserFileVO> userFileVOS = this.fileDownloadService.showAcceptedFiles(userName);
        GetMessage getMessage = new GetMessage();

        if(!userFileVOS.isEmpty()){
            getMessage.setMessage("List");
            getMessage.setData(userFileVOS);
        }

        return ResponseEntity.status(HttpStatus.OK).body(getMessage);
    }

    @Operation(summary = "선택 파일 다운로드", description = "파일 id를 통해 파일을 다운로드 합니다.")
    @GetMapping("/file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile( @PathVariable("id") Long id) throws IOException {

        if(id<0){
            throw new RuntimeException("잘못된 매개변수를 입력하셨습니다. id값은 0보다 커야 합니다.");
        }

        String userName = authService.getUserNameWeb();

        fileDownloadService.setParameter(userName, id);

        String fileName = fileDownloadService.getFileName();

        UserFileVO userFileVO = fileDownloadService.getUserFileVO(id);
        Resource resource = fileDownloadService.downloadFile(id);

        if(userFileVO != null && !(fileName.isEmpty()) && resource.isFile()){

            fileDownloadService.decreaseCountNum(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
            headers.setContentLength(resource.contentLength());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }

        return ResponseEntity.status(HttpStatus.OK).build();

    }

}
