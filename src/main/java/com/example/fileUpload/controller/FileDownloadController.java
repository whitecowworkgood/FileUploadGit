package com.example.fileUpload.controller;


import com.example.fileUpload.message.GetMessage;
import com.example.fileUpload.model.FileVO;
import com.example.fileUpload.repository.EncryptDao;
import com.example.fileUpload.service.FileDownloadService;
import com.example.fileUpload.service.FileEncryptService;
import com.example.fileUpload.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/download")
@Tag(name = "FileDownload", description = "파일 다운로드 API 구성")
public class FileDownloadController {
    @Value("${Save-Directory}")
    private String dir;

    private final FileUploadService fileUploadService;
    private final FileDownloadService fileDownloadService;

    private final EncryptDao encryptDao;

    @Operation(summary = "허가받은 파일 출력", description = "관리자로 부터 다운로드 허가 받은 파일을 출력합니다.")
    @GetMapping("/files/{userName}")
    @ResponseBody
    public ResponseEntity<GetMessage> showFiles(@PathVariable("userName") String userName) throws IOException {

        List<FileVO> fileVOS = fileDownloadService.showAcceptedFiles(userName);
        GetMessage getMessage = new GetMessage();

        if(!fileVOS.isEmpty()){
            getMessage.setMessage("List");
            //getMessage.setHttpStatus(200);
            getMessage.setData(fileVOS);
        }

        return ResponseEntity.status(HttpStatus.OK).body(getMessage);
    }
    @Operation(summary = "선택 파일 다운로드", description = "파일 id를 통해 파일을 다운로드 합니다.")
    @GetMapping("/file/{userName}/{fileName}")
    @ResponseBody
    public void downloadFile(@PathVariable("userName") String userName, @PathVariable("fileName") String fileName,
                             HttpServletResponse response) {

        //나중에 서비스로 구현하기 - 허가 유무와, 카운트 갯수로 다운로드 가능 여부

        OutputStream os = null;
        // 파일 입력 객체 생성
        FileInputStream fis = null;

        try{
            File f = new File(dir+File.separator+"download"+File.separator+userName+File.separator, fileName);
            // file 다운로드 설정
            response.setContentType("application/download");
            response.setContentLength((int)f.length());
            response.setHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"");

            os = response.getOutputStream();
            // 파일 입력 객체 생성
            fis = new FileInputStream(f);
            FileCopyUtils.copy(fis, os);

        }catch(IOException e){
            ExceptionUtils.getStackTrace(e);

        }finally{
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(os);
        }

    }

}
