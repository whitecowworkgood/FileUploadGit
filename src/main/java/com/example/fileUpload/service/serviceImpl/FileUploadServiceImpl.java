package com.example.fileUpload.service.serviceImpl;


import com.example.fileUpload.dto.FileDto;
import com.example.fileUpload.entity.FileEntity;
import com.example.fileUpload.repository.SaveFileRepository;
import com.example.fileUpload.service.FileUploadService;
import com.example.fileUpload.unit.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.fileUpload.unit.FileUtil.fileOleParser;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileUploadServiceImpl implements FileUploadService {
    private final SaveFileRepository saveFileRepository;
    private final ModelMapper modelMapper;

    @Value("${Save-Directory}")
    private String dir;
    //추후 프로젝트 경로나, c:\\경로에 폴더가 있는지 확인 후, 없으면 폴더 생성 후 파일 전송하기

    @Override
    public boolean fileUpload(FileDto fileDto) {
        try{
            if(!fileDto.getFileData().isEmpty()){
                boolean isValid = FileUtil.valuedDocFile(fileDto);

                if(isValid){
                    String fullPath = dir + fileDto.getFileName();

                    if(FileUtil.isValidPath(dir, fullPath)){
                        //return false;
                        fileDto.getFileData().transferTo(new File(fullPath));
                        //IOException
                        FileEntity fileEntity = modelMapper.map(fileDto, FileEntity.class);

                        if(fileEntity == null){
                            throw new RuntimeException();
                        }
                        saveFileRepository.save(fileEntity);
                        //RuntimeException
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));

        }catch (RuntimeException e){
            log.error(ExceptionUtils.getStackTrace(e));
            //db저장시 예외처리를 구현하면 됨 - 저장된 파일 삭제 처리 추가하기
        }
        return false;
    }

    @Override
    public List<FileDto> printAll() {

        List<FileEntity> fileEntities = saveFileRepository.findAll();

        return fileEntities.stream()
                .map(fileEntity -> modelMapper.map(fileEntity, FileDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public FileDto printOne(Long id) {
        Optional<FileEntity> optionalFileEntity = saveFileRepository.findById(id);

        return optionalFileEntity.map(fileEntity -> modelMapper.map(fileEntity, FileDto.class))
                .orElse(null);
    }

    @Override
    public boolean deleteOne(Long id) {

        FileEntity fileEntity = saveFileRepository.findById(id).orElse(null);
        if (fileEntity == null) {
            return false;
        }

        String fileName = fileEntity.getFileName();
        String fullPath = dir + fileName;

        if(!FileUtil.isValidPath(dir, fullPath)){
            return false;
        }

        File file = new File(fullPath);

        if (!(file.exists() && file.delete())) {
            log.warn(fileName + " 파일 삭제 오류 발생 또는 파일이 없음, DB에서 정보 삭제");
            //return true;
        }
        saveFileRepository.deleteById(fileEntity.getId());
        return true;
    }

//    @Override
//    //@Scheduled(fixedRate = 60000)
//    public void Scheduler() {
//       // log.info("스케줄러 가동");
//    }
}