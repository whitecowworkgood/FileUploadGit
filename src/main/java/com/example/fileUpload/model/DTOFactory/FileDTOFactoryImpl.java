package com.example.fileUpload.model.DTOFactory;

import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class FileDTOFactoryImpl implements FileDTOFactory{

    @Value("${Save-Directory}")
    private String baseDir;

    @Value("${Ole-Directory}")
    private String oleDir;

    @Value("${Temp-Diractory}")
    private String tempDir;

    @Override
    public synchronized FileDto generateUploadDtoOf(MultipartFile multipartFile, Long countNum, String comment) throws IOException {

        String uuidName = UUID.randomUUID().toString();

        String uuidFileName = generateUuidFileName(multipartFile.getOriginalFilename(), uuidName);
        String fileSavePath = generateFileSavePath(uuidFileName);
        String fileOlePath = generateFileOlePath(uuidName);
        String dividedComment = dividedCommentSize(comment);

        String tempFilePath = String.format("%s%s%s", tempDir,File.separator, uuidFileName);

        multipartFile.transferTo(Path.of(tempFilePath));

        return FileDto.builder()
                .UUIDFileName(uuidFileName)
                .originFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()))
                .fileSize(multipartFile.getSize())
                .fileType(multipartFile.getContentType())
                .fileSavePath(fileSavePath)
                .fileOlePath(fileOlePath)
                .countNum(countNum)
                .fileTempPath(tempFilePath)
                .comment(dividedComment)
                .build();

    }


    private String generateUuidFileName(String originalFileName, String uuidName) {
        return new StringBuffer(uuidName)
                .append(".")
                .append(FileUtil.getFileExtension(originalFileName)).toString();
    }

    private String generateFileSavePath(String uuidFileName) {
        return new StringBuffer(this.baseDir)
                .append(File.separator)
                .append(uuidFileName).toString();
    }

    private String generateFileOlePath(String uuidName) {
        return new StringBuffer(this.oleDir)
                .append(File.separator)
                .append(uuidName)
                .append(File.separator).toString();
    }

    private String dividedCommentSize(String comment) {
        int maxLength = 100;
        if (comment != null && comment.length() > maxLength) {
            comment = comment.substring(0, maxLength);
        }
        return comment;
    }
}