package com.example.fileUpload.model.DTOFactory;

import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;
import java.util.UUID;


@Slf4j
@Component
public class FileDTOFactoryImpl implements FileDTOFactory{

    @Value("${Save-Directory}")
    private String baseDir;

    @Override
    public synchronized FileDto generateUploadDtoOf(MultipartFile multipartFile, Long countNum, String comment) {

        String uuidName = UUID.randomUUID().toString();
        String uuidFileName = generateUuidFileName(multipartFile, uuidName);
        String fileSavePath = generateFileSavePath(uuidFileName);
        String fileOlePath = generateFileOlePath(uuidName);
        String dividedComment = dividedCommentSize(comment);

        return FileDto.builder()
                .UUIDFileName(uuidFileName)
                .originFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()))
                .fileSize(multipartFile.getSize())
                .fileType(multipartFile.getContentType())
                .fileSavePath(fileSavePath)
                .fileOlePath(fileOlePath)
                .countNum(countNum)
                .fileData(multipartFile)
                .comment(dividedComment)
                .build();

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

    private String dividedCommentSize(String comment) {
        int maxLength = 100;
        if (comment != null && comment.length() > maxLength) {
            comment = comment.substring(0, maxLength);
        }
        return comment;
    }
}