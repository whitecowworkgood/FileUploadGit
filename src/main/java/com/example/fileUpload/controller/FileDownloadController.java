package com.example.fileUpload.controller;


import com.example.fileUpload.repository.EncryptDao;
import com.example.fileUpload.service.FileEncryptService;
import com.example.fileUpload.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/download")
@Tag(name = "FileDownload", description = "파일 다운로드 API 구성")
public class FileDownloadController {

    private final FileUploadService fileUploadService;
    private final FileEncryptService fileEncryptService;
    private final EncryptDao encryptDao;

    String kek =null;
    byte[] buffer = null;
    int bytesRead =0;

    @Operation(summary = "선택 파일 다운로드", description = "파일 id를 통해 파일을 다운로드 합니다.")
    @GetMapping("/file/{id}")
    @ResponseBody
    public void downloadFile(@PathVariable("id") Long id) throws IOException {

       fileEncryptService.decryptFile(id);
        //System.out.println(kek);
        //fileEncryptService.decryptFile(encryptDao.findPrivateKey(kek));
        //kek=null;

    }

    @SneakyThrows
    private void checkBytesReadSize(){
        if (bytesRead > 0) {
            //kek = fileEncryptService.decryptKEK(buffer);

            //fileEncryptService.decryptFile(encryptDao.findPrivateKey(kek));
            //encryptDao.findPrivateKey(kek);

        } else {
            System.out.println("파일이 비어 있습니다.");
        }
    }

    private void ReadFileKEK(){
        String filePath = "C:\\files\\aa58e51c-d851-4bbf-aac3-d8842a6d9603.docx";

        try (RandomAccessFile file = new RandomAccessFile(filePath, "rw")) {
            // 파일 크기 확인
            long fileSize = file.length();

            // 읽을 바이트 수 설정 (하위 400바이트)
            int bufferSize = 400;
            long startPosition = Math.max(0, fileSize - bufferSize);

            // 파일 포인터를 설정된 위치로 이동
            file.seek(startPosition);

            // 읽어올 데이터를 저장할 배열 생성
            buffer = new byte[bufferSize];

            // 데이터 읽기
            bytesRead = file.read(buffer, 0, bufferSize);

            // 읽은 데이터 출력
            checkBytesReadSize();


        } catch (IOException  e) {

            ExceptionUtils.getStackTrace(e);

        }
        finally {
            //kek=null;
            buffer = null;
            bytesRead =0;
        }
    }

    private void moveFileToTemp() throws IOException {
        String filePath = "C:\\files\\aa58e51c-d851-4bbf-aac3-d8842a6d9603.docx";
        String tempPath = "C:\\files\\temp\\aa58e51c-d851-4bbf-aac3-d8842a6d9603.docx";

        try (RandomAccessFile file = new RandomAccessFile(filePath, "rw");
             FileOutputStream newFile = new FileOutputStream(tempPath)) {
            long originalFileSize = file.length();

            // 원본 파일에서 하위 400바이트를 제외한 내용을 새 파일로 복사
            long newPosition = Math.max(0, originalFileSize - 400);
            file.setLength(newPosition);

            buffer = new byte[1024];
            //int bytesRead;
            while ((bytesRead = file.read(buffer)) != -1) {
                newFile.write(buffer, 0, bytesRead);
            }

        } catch (IOException  e) {

            ExceptionUtils.getStackTrace(e);

        }
        finally {
            buffer=null;
            bytesRead=0;
            Files.deleteIfExists(Path.of(filePath));
        }

    }
}
