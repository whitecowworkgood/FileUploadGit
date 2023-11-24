package com.example.fileUpload.controller;

/*import com.example.fileUpload.message.GetMessage;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/simul")
@Tag(name = "Simul", description = "테스트용 컨트롤러")
public class TestController {
    private final TestService testService;

    @Operation(summary = "파일 접근", description = "파일의 경로를 받습니다.")
    @GetMapping("")
    public ResponseEntity<GetMessage> uploadFile(@RequestParam(value = "Path") String Path) throws IOException {

        String mimeType = Files.probeContentType(java.nio.file.Path.of(Path));

        FileDto fileDto= FileDto.builder()
                .fileSavePath(Path)
                .fileType(mimeType)
                .fileOlePath("C:/temp/"+ UUID.randomUUID()+ File.separator)
                .originFileName("test.test")//기존 코드에 원본 파일명 db 저장 코드 때문에 넣음, db 저장을 안해도 오류가 발생 해서 넣음
                .build();

        List<String> list = testService.doService(fileDto);

        GetMessage getMessage = new GetMessage();
        getMessage.setMessage("결과");
        getMessage.setData(list.toString());

        return ResponseEntity.status(HttpStatus.OK).body(getMessage);

    }
}*/
