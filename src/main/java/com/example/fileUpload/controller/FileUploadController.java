package com.example.fileUpload.controller;


import com.example.fileUpload.dto.FileDto;
import com.example.fileUpload.dto.Message;
import com.example.fileUpload.dto.StatusEnum;
import com.example.fileUpload.entity.FileEntity;
import com.example.fileUpload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.Charset;


//@RestController
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/file")

public class FileUploadController {

    private final FileUploadService fileUploadService;


    @GetMapping("/")
    public String root(){
        return "redirect:/file/upload";
    }

    @GetMapping("/upload")
    public String printForm(){
        log.debug("form 출력");
        return "file-form";
    }

    @GetMapping("/uploads")
    @ResponseBody
    public ResponseEntity<Message> printFiles(){
        return fileUploadService.printAll();

    }

    @GetMapping("/upload/{id}")
    @ResponseBody
    public ResponseEntity<Message> printFile(@PathVariable("id") Long id){
        FileEntity fileEntity = fileUploadService.printOne(id);

        if(fileEntity != null){

            FileDto fileDto = FileDto.builder()
                    .fileName(fileEntity.getFileName())
                    .fileType(fileEntity.getFileType())
                    .fileSize(fileEntity.getFileSize()).build();

            Message message = new Message();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            message.setStatus(StatusEnum.OK);
            message.setMessage("성공 코드");
            message.setData(fileDto);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }else {
            Message errorMessage = new Message();
            errorMessage.setMessage("파일을 찾을 수 없습니다.");  // 수정이 필요할 수 있음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

    }
    // GET 방식만 수정해야 하는지 확인하기
    @DeleteMapping("/upload")
    public String DeleteFile(@RequestParam("id") Long id){

        fileUploadService.deleteOne(id);

        return "redirect:/file/uploads";

    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file){
        fileUploadService.fileUpload(file);
        log.debug("파일 저장");
        return "redirect:/file/uploads";
        //return "OK";
    }
}
