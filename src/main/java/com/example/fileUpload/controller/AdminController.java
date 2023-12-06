package com.example.fileUpload.controller;

import com.example.fileUpload.message.ResultMessage;
import com.example.fileUpload.model.File.FileVO;

import com.example.fileUpload.model.Ole.OleVO;
import com.example.fileUpload.service.AdminService;
import com.example.fileUpload.service.FileEncryptService;
import com.example.fileUpload.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자용 컨트롤러")
public class AdminController {

    private final AdminService adminService;
    private final FileUploadService fileUploadService;
    private final FileEncryptService fileEncryptService;

    @Operation(summary = "허가 대기 중인 파일 출력", description = "허가 대기 중인 파일 출력합니다.")
    @GetMapping("/files")
    @ResponseBody
    public ResponseEntity<String> downloadFile(){

        List<FileVO> fileVOS = this.adminService.printBeforeAcceptFiles();

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResultMessage.getInstance().fileListOf("waiting_List", fileVOS));
    }

    @Operation(summary = "선택 파일 OLE 파일 조회", description = "파일 id를 통해 파일에 대한 OLE 정보를 출력 한다.")
    @GetMapping("/file/{id}/ole")
    @ResponseBody
    public ResponseEntity<String> printOle(@Valid @PathVariable("id") @Min(value = 0)Long id) {

        List<OleVO> oleVOS = this.fileUploadService.printOleAll(id);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResultMessage.getInstance().oleListOf("ole_List", oleVOS));
    }

    @Operation(summary = "파일의 다운로드를 허가", description = "파일의 다운로드를 허가합니다..")
    @PutMapping("/accept")
    @ResponseBody
    public ResponseEntity<String> acceptFile(@Valid @RequestParam("id") @Min(value = 0)Long id){

        String message = handleDatabaseAndFileProcessing(id);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResultMessage.getInstance().acceptOf(message));
    }

    private String handleDatabaseAndFileProcessing(Long id){
        if(fileUploadService.printFileOne(id).isActive()){
            return "Already Changed";
        }
        this.adminService.acceptFile(id);
        this.fileEncryptService.decryptFile(id);
        return "Change Completed";
    }
}
