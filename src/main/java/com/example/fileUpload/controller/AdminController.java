package com.example.fileUpload.controller;

import com.example.fileUpload.message.GetMessage;
import com.example.fileUpload.model.FileVO;
import com.example.fileUpload.repository.FileDao;
import com.example.fileUpload.service.AdminService;
import com.example.fileUpload.service.FileEncryptService;
import com.example.fileUpload.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자용 컨트롤러")
public class AdminController {

    private final AdminService adminService;
    private final FileEncryptService fileEncryptService;

    @Operation(summary = "허가 대기 중인 파일 출력", description = "허가 대기 중인 파일 출력합니다.")
    @GetMapping("/files")
    @ResponseBody
    public ResponseEntity<GetMessage> downloadFile(){

        List<FileVO> fileVOS = adminService.printBeforeAcceptFiles();
        GetMessage getMessage = new GetMessage();

        if(!fileVOS.isEmpty()){
            getMessage.setMessage("List");
            //getMessage.setHttpStatus(200);
            getMessage.setData(fileVOS);
        }

        return ResponseEntity.status(HttpStatus.OK).body(getMessage);
    }

    @Operation(summary = "파일의 다운로드를 허가", description = "파일의 다운로드를 허가합니다..")
    @PutMapping("/accept/{id}")
    @ResponseBody
    public void acceptFile(@PathVariable("id") Long id){
        adminService.acceptFile(id);
        fileEncryptService.decryptFile(id);
    }

}
