package com.example.fileUpload.controller;


import com.example.fileUpload.model.OleDto;
import com.example.fileUpload.message.PostDeleteMessage;
import com.example.fileUpload.model.FileDto;
import com.example.fileUpload.message.GetMessage;
import com.example.fileUpload.model.FileVO;
import com.example.fileUpload.service.FileUploadService;
import com.example.fileUpload.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/upload")
@Tag(name = "FileUpload", description = "파일 업로드 API 구성")
public class FileUploadController {

    @Value("${Save-Directory}")
    private String dir;
    private final FileUploadService fileUploadService;

/*
 * 해당 영역은 페이지를 넘겨만 주는 영역
 */
/*
    @GetMapping("/")
    public String root(){
        return "redirect:/file/upload";
    }

    @GetMapping("/upload")
    public String printForm(){
        log.debug("form 출력");
        return "file-form";
    }
*/
    /*
     * 해당 영역은 API 영역
     */

    /**
     * 전체 파일을 조회합니다.
     *
     * @return ResponseEntity<GetMessage> 파일 목록 조회 결과를 반환합니다.
     */
    @Operation(summary = "전체 파일 조회", description = "저장된 파일 정보들을 조회 합니다.")
    @GetMapping("/files")
    public ResponseEntity<GetMessage> printFiles(){

        List<FileVO> fileVOS = fileUploadService.printFileAll();
        GetMessage getMessage = new GetMessage();

        if(!fileVOS.isEmpty()){
            getMessage.setMessage("List");
            //getMessage.setHttpStatus(200);
            getMessage.setData(fileVOS);
        }
        return ResponseEntity.status(HttpStatus.OK).body(getMessage);
    }

    /**
     * 선택한 파일을 조회합니다.
     *
     * @param id 조회할 파일의 ID입니다.
     * @return ResponseEntity<GetMessage> 선택한 파일의 정보를 반환합니다.
     */
    @Operation(summary = "선택 파일 조회", description = "파일 id를 통해 파일 정보를 조회 합니다.")
    @GetMapping("/file/{id}")
    @ResponseBody
    public ResponseEntity<GetMessage> printFile(@PathVariable("id") Long id){

        FileVO fileVO = fileUploadService.printFileOne(id);

        GetMessage getMessage = new GetMessage();

        if(fileVO != null){
            getMessage.setMessage("File");
            //getMessage.setHttpStatus(200);
            getMessage.setData(fileVO);
        }
        return ResponseEntity.status(HttpStatus.OK).body(getMessage);
    }


    /**
     * 선택한 파일의 OLE 정보를 조회합니다.
     *
     * @param id 조회할 파일의 ID입니다.
     * @return ResponseEntity<GetMessage> 선택한 파일의 OLE 정보를 반환합니다.
     */
    @Operation(summary = "선택 파일 OLE 파일 조회", description = "파일 id를 통해 파일에 대한 OLE 정보를 출력 한다.")
    @GetMapping("/file/{id}/ole")
    @ResponseBody
    public ResponseEntity<GetMessage> printOle(@PathVariable("id") Long id) {
        List<OleDto> oleDtoList = fileUploadService.printOleAll(id);
        GetMessage getMessage = new GetMessage();

        if(!oleDtoList.isEmpty()){
            getMessage.setMessage("FileOle");
            getMessage.setData(oleDtoList);
        }
        return ResponseEntity.status(HttpStatus.OK).body(getMessage);
    }

    /**
     * 파일을 삭제합니다.
     *
     * @param id 삭제할 파일의 ID입니다.
     * @return ResponseEntity<PostDeleteMessage> 파일 삭제 결과를 반환합니다.
     */
    @Operation(summary = "파일 삭제", description = "파일 id를 통해 파일 정보를 삭제 합니다.")
    @DeleteMapping("")
    public ResponseEntity<PostDeleteMessage> DeleteFile(@RequestParam("id") Long id){

        boolean deleteResult = fileUploadService.deleteOne(id);
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();

        if(deleteResult){
            postDeleteMessage.setMessage("DeleteOk");
            //postDeleteMessage.setHttpStatus(200);
        }
        return ResponseEntity.status(HttpStatus.OK).body(postDeleteMessage);
    }

    /**
     * 파일을 업로드합니다.
     *
     * @param file 업로드할 파일입니다.
     * @return ResponseEntity<PostDeleteMessage> 파일 업로드 결과를 반환합니다.
     */
    @Operation(summary = "파일 업로드", description = "파일을 저장 합니다.")
    @PostMapping("")
    public ResponseEntity<PostDeleteMessage> uploadFile(@RequestParam("file") MultipartFile file){

        String uuidName = UUID.randomUUID().toString();

        FileDto fileDto = FileDto.builder()
                .UUIDFileName(uuidName+FileUtil.getFileExtension(file))
                .originFileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileSavePath(dir+File.separator+uuidName+FileUtil.getFileExtension(file))
                .fileOlePath(dir+File.separator+"ole"+File.separator+ uuidName + File.separator)
                .fileData(file)
                .build();

        boolean createResult = fileUploadService.fileUpload(fileDto);
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();

        if(createResult){
            postDeleteMessage.setMessage("CREATE");
            //postDeleteMessage.setHttpStatus(201);
        }
        return ResponseEntity.status(HttpStatus.OK).body(postDeleteMessage);
    }
}
