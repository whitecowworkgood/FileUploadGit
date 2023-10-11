package com.example.fileUpload.controller;

import com.example.fileUpload.message.GetMessage;
import com.example.fileUpload.model.File.FileVO;

import com.example.fileUpload.model.Ole.OleVO;
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
    public ResponseEntity<GetMessage> downloadFile(){

        List<FileVO> fileVOS = this.adminService.printBeforeAcceptFiles();
        GetMessage getMessage = new GetMessage();

        if(!fileVOS.isEmpty()){
            getMessage.setMessage("List");
            //getMessage.setHttpStatus(200);
            getMessage.setData(fileVOS);
        }

        return ResponseEntity.status(HttpStatus.OK).body(getMessage);
    }

    @Operation(summary = "선택 파일 OLE 파일 조회", description = "파일 id를 통해 파일에 대한 OLE 정보를 출력 한다.")
    @GetMapping("/file/{id}/ole")
    @ResponseBody
    public ResponseEntity<GetMessage> printOle(@PathVariable("id") Long id) {
        List<OleVO> oleVOS = this.fileUploadService.printOleAll(id);
        GetMessage getMessage = new GetMessage();

        if(!oleVOS.isEmpty()){
            getMessage.setMessage("FileOle");
            getMessage.setData(oleVOS);
        }
        return ResponseEntity.status(HttpStatus.OK).body(getMessage);
    }

    @Operation(summary = "파일의 다운로드를 허가", description = "파일의 다운로드를 허가합니다..")
    @PutMapping("/accept")
    @ResponseBody
    public void acceptFile(@RequestParam("id")  Long id){
        this.adminService.acceptFile(id);
        this.fileEncryptService.decryptFile(id);
    }

}
