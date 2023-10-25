package com.example.fileUpload.controller;


import com.example.fileUpload.message.PostDeleteMessage;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.service.FileUploadService;
import com.example.fileUpload.service.serviceImpl.AuthService;
import com.example.fileUpload.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.io.File;
import java.util.Objects;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/upload")
@Tag(name = "FileUpload", description = "파일 업로드 API 구성")
public class FileUploadController {

    @Value("${Save-Directory}")
    private String baseDir;
    private final FileUploadService fileUploadService;
    private final AuthService authService;

    /**
     * 파일을 업로드합니다.
     *
     * @param file 업로드할 파일입니다.
     * @return ResponseEntity<PostDeleteMessage> 파일 업로드 결과를 반환합니다.
     */
    @Operation(summary = "파일 업로드", description = "파일을 저장 합니다.")
    @PostMapping("")
    public ResponseEntity<PostDeleteMessage> uploadFile(@RequestParam("countNum") Long countNum,
                                                        @RequestParam(value = "comment", required = false, defaultValue = "null") String comment,
                                                        @RequestParam("file") MultipartFile file/*, @RequestParam(value = "encryption", defaultValue = "true") boolean encryption*/) {
        String userName = authService.getUserNameWeb();
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();

        try {

            String uuidName = UUID.randomUUID().toString();

            String uuidFileName = generateUuidFileName(file, uuidName);
            String fileSavePath = generateFileSavePath(uuidFileName);
            String fileOlePath = generateFileOlePath(uuidName);

            FileDto fileDto = createFileDto(file, uuidFileName, fileSavePath, fileOlePath, countNum, userName, comment/*, encryption*/);

            fileUploadService.fileUpload(fileDto);

        } catch (Exception e) {
            ExceptionUtils.getStackTrace(e);
            postDeleteMessage.setMessage("UNPROCESSABLE_ENTITY");
        }

        return ResponseEntity.status(HttpStatus.OK).body(postDeleteMessage);
    }

    private String generateUuidFileName(MultipartFile file, String uuidName) {
        return new StringBuffer(uuidName)
                .append(FileUtil.getFileExtension(file)).toString();
    }

    private String generateFileSavePath(String uuidFileName) {
        return new StringBuffer(this.baseDir)
                .append(File.separator)
                .append(uuidFileName).toString();
    }

    private String generateFileOlePath(String uuidName) {
        return new StringBuffer(this.baseDir)
                .append(File.separator)
                .append("ole")
                .append(File.separator)
                .append(uuidName)
                .append(File.separator).toString();
    }

    private FileDto createFileDto(MultipartFile file, String uuidFileName, String fileSavePath, String fileOlePath, Long countNum, String userName, String comment/*, boolean encryption*/) {
        return FileDto.builder()
                .UUIDFileName(uuidFileName)
                .originFileName(HtmlUtils.htmlEscape(Objects.requireNonNull(file.getOriginalFilename())))
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileSavePath(fileSavePath)
                .fileOlePath(fileOlePath)
                .countNum(countNum)
                .fileData(file)
                .userName(userName)
                .comment(HtmlUtils.htmlEscape(comment))
                //.isEncrypt(encryption)
                .build();
    }

    /*public ResponseEntity<PostDeleteMessage> uploadFile(@RequestParam("countNum") Long countNum, @RequestParam("userName") String userName,
                                                        @RequestParam(value = "comment", required = false) String comment,
                                                        @RequestParam("file") MultipartFile file, @RequestParam("encryption") boolean encryption){

        String uuidName = UUID.randomUUID().toString();

        if(comment.isEmpty()){
            comment=userName;
        }

        String uuidFileName = stringBuffer.append(uuidName)
                .append(FileUtil.getFileExtension(file)).toString();
        stringBuffer.delete(0, stringBuffer.length());


        String fileSavePath = stringBuffer.append(this.baseDir)
                .append(File.separator)
                .append(uuidFileName).toString();
        stringBuffer.delete(0, stringBuffer.length());


        String fileOlePath = stringBuffer.append(this.baseDir)
                .append(File.separator)
                .append("ole")
                .append(File.separator)
                .append(uuidName)
                .append(File.separator).toString();
        stringBuffer.delete(0, stringBuffer.length());


        FileDto fileDto = FileDto.builder()
                .UUIDFileName(uuidFileName)
                .originFileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileSavePath(fileSavePath)
                .fileOlePath(fileOlePath)
                .countNum(countNum)
                .fileData(file)
                .userName(userName)
                .comment(comment)
                .isEncrypt(encryption)
                .build();

        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();
        try{

            fileUploadService.fileUpload(fileDto);

        }catch (Exception e){
            ExceptionUtils.getStackTrace(e);
            postDeleteMessage.setMessage("UNPROCESSABLE_ENTITY");
        }

        return ResponseEntity.status(HttpStatus.OK).body(postDeleteMessage);
    }*/

    /**
     * 전체 파일을 조회합니다.
     *
     * @return ResponseEntity<GetMessage> 파일 목록 조회 결과를 반환합니다.
     */
   /* @SneakyThrows
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
    }*/

    /**
     * 선택한 파일을 조회합니다.
     *
     * @param id 조회할 파일의 ID입니다.
     * @return ResponseEntity<GetMessage> 선택한 파일의 정보를 반환합니다.
     */
    /*@Operation(summary = "선택 파일 조회", description = "파일 id를 통해 파일 정보를 조회 합니다.")
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
*/

    /**
     * 선택한 파일의 OLE 정보를 조회합니다.
     *
     * @param id 조회할 파일의 ID입니다.
     * @return ResponseEntity<GetMessage> 선택한 파일의 OLE 정보를 반환합니다.
     */
    /*@Operation(summary = "선택 파일 OLE 파일 조회", description = "파일 id를 통해 파일에 대한 OLE 정보를 출력 한다.")
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
    }*/

    /**
     * 파일을 삭제합니다.
     *
     * @param id 삭제할 파일의 ID입니다.
     * @return ResponseEntity<PostDeleteMessage> 파일 삭제 결과를 반환합니다.
     */
    /*@Operation(summary = "파일 삭제", description = "파일 id를 통해 파일 정보를 삭제 합니다.")
    @DeleteMapping("")
    public ResponseEntity<PostDeleteMessage> DeleteFile(@RequestParam("id") Long id){

        boolean deleteResult = this.fileUploadService.deleteOne(id);
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();

        if(deleteResult){
            postDeleteMessage.setMessage("DeleteOk");
        }
        return ResponseEntity.status(HttpStatus.OK).body(postDeleteMessage);
    }*/

    //나중에 파일업로드 여러개를 구현하는 코드 - 아직 구현계획 없음
    /*@Operation(summary = "파일 업로드", description = "여러개의 파일을 저장합니다.")
    @PostMapping("/uploads")
    public ResponseEntity<PostDeleteMessage> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        List<FileDto> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String uuidName = UUID.randomUUID().toString();

            FileDto fileDto = FileDto.builder()
                    .UUIDFileName(uuidName + FileUtil.getFileExtension(file))
                    .originFileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .fileSavePath(dir + File.separator + uuidName + FileUtil.getFileExtension(file))
                    .fileOlePath(dir + File.separator + "ole" + File.separator + uuidName + File.separator)
                    .fileData(file)
                    .build();

            boolean createResult = fileUploadService.fileUpload(fileDto);

            if (createResult) {
                uploadedFiles.add(fileDto);
            }
        }

        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();
        postDeleteMessage.setMessage("CREATE");

        return ResponseEntity.status(HttpStatus.OK).body(postDeleteMessage);
    }*/

}
