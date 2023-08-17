package com.example.fileUpload.controller;


import com.example.fileUpload.dto.FileDto;
import com.example.fileUpload.dto.Message;
import com.example.fileUpload.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


//@RestController
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/file")
@Tag(name = "FileUpload", description = "파일 업로드 API 구성")
public class FileUploadController {

    private final FileUploadService fileUploadService;

/*
 * 해당 영역은 페이지를 넘겨만 주는 영역
 */
//    @GetMapping("/")
//    public String root(){
//        return "redirect:/file/upload";
//    }
//
//    @GetMapping("/upload")
//    public String printForm(){
//        log.debug("form 출력");
//        return "file-form";
//    }


    /*
     * 해당 영역은 API 영역
     */
    @Operation(summary = "전체 파일 조회", description = "저장된 파일정보들을 조회합니다.")
    @GetMapping("/uploads")
    @ResponseBody
    //HttpServletRequest httpServletRequest 이것으로 헤더들 보고 설정할 수 있음.
    public ResponseEntity<Message> printFiles(){

        List<FileDto> fileDtos = fileUploadService.printAll();
//        if(fileDtos.isEmpty()){
//            return ;이런식으로 처음 예외를 구현을 하면, 나머지 else도 필요 없고, 나머지는 참인 경우만 해결됨
//        }
        if(!fileDtos.isEmpty()){
            Message message = new Message();

            message.setMessage("파일 목록 조회 성공");
            message.setData(fileDtos);

            //return new ResponseEntity<>(message, headers, HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }else{
            Message errorMessage = new Message();
            errorMessage.setMessage("파일 목록 조회 실패");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
            //return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
        }

    }
    @Operation(summary = "선택 파일 조회", description = "파일 id를 통해 파일정보를 조회합니다.")
    @GetMapping("/upload/{id}")
    @ResponseBody
    public ResponseEntity<Message> printFile(@PathVariable("id") Long id){

        FileDto fileDto = fileUploadService.printOne(id);

        if(fileDto != null){

            Message message = new Message();

            message.setMessage("파일 조회 성공");
            message.setData(fileDto);

            return ResponseEntity.status(HttpStatus.OK).body(message);
        }else {
            Message errorMessage = new Message();
            errorMessage.setMessage("파일 조회 실패");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
           // return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
        }

    }

    @Operation(summary = "파일 삭제", description = "파일 id를 통해 파일정보를 삭제합니다.")
    @DeleteMapping("/upload")
    public ResponseEntity<Message> DeleteFile(@RequestParam("id") Long id){

        boolean deleteResult = fileUploadService.deleteOne(id);

        if(deleteResult){
            Message successMessage = new Message();
            successMessage.setMessage("파일 삭제 성공");
            return ResponseEntity.status(HttpStatus.OK).body(successMessage);

        }else{
            Message failedMessage = new Message();
            failedMessage.setMessage("파일 삭제 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failedMessage);
        }
    }
    @Operation(summary = "전체 파일 생성", description = "파일을 저장합니다.")
    @PostMapping("/upload")
    public ResponseEntity<Message> uploadFile(@RequestParam("file") MultipartFile file){

        FileDto fileDto = FileDto.builder()
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileData(file)
                .build();

        boolean createResult = fileUploadService.fileUpload(fileDto);

        if(createResult){
            Message successMessage = new Message();
            successMessage.setMessage("파일 업로드 성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(successMessage);

        }else{
            Message failedMessage = new Message();
            failedMessage.setMessage("파일 업로드 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failedMessage);
        }

    }
}
