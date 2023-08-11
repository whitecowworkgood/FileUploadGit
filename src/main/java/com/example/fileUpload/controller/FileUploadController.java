package com.example.fileUpload.controller;


import com.example.fileUpload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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
    public Object printFiles(){
        return fileUploadService.printAll();

    }

    @GetMapping("/upload/{id}")
    @ResponseBody
    public Object printFiles(@PathVariable("id") Long id){
        return fileUploadService.printOne(id);

    }

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
