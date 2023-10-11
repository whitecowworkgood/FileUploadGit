package com.example.fileUpload.controller;


import com.example.fileUpload.message.GetMessage;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.File.UserFileVO;
import com.example.fileUpload.repository.EncryptDao;
import com.example.fileUpload.service.FileDownloadService;
import com.example.fileUpload.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.User;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/download")
@Tag(name = "FileDownload", description = "파일 다운로드 API 구성")
public class FileDownloadController {

    private final FileDownloadService fileDownloadService;

    @Operation(summary = "허가받은 파일 출력", description = "관리자로 부터 다운로드 허가 받은 파일을 출력합니다.")
    @GetMapping("/files/{userName}")
    @ResponseBody
    public ResponseEntity<GetMessage> showFiles(@PathVariable("userName") String userName) {

        List<UserFileVO> userFileVOS = this.fileDownloadService.showAcceptedFiles(userName);
        GetMessage getMessage = new GetMessage();

        if(!userFileVOS.isEmpty()){
            getMessage.setMessage("List");
            //getMessage.setHttpStatus(200);
            getMessage.setData(userFileVOS);
        }

        return ResponseEntity.status(HttpStatus.OK).body(getMessage);
    }

    @Operation(summary = "선택 파일 다운로드", description = "파일 id를 통해 파일을 다운로드 합니다.")
    @GetMapping("/file/{userName}/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable("userName") String userName, @PathVariable("id") Long id) {

        fileDownloadService.setParameter(userName, id);

        String fileName = fileDownloadService.getFileName();

        UserFileVO userFileVO = fileDownloadService.getUserFileVO(id);

        if(userFileVO != null && !(fileName.isEmpty())){

            //count -- 쿼리문 구현하기
            fileDownloadService.decreaseCountNum(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(fileDownloadService.downloadFile(id));
        }

        return ResponseEntity.status(HttpStatus.OK).build();

    }

}
