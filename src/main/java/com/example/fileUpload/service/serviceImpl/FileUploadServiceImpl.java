package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.documentParser.FileProcessor;
import com.example.fileUpload.model.File.FileDto;
import com.example.fileUpload.model.Ole.OleDto;
import com.example.fileUpload.model.File.FileVO;

import com.example.fileUpload.model.Ole.OleVO;
import com.example.fileUpload.repository.FileDao;
import com.example.fileUpload.repository.OleDao;
import com.example.fileUpload.service.FileEncryptService;
import com.example.fileUpload.service.FileUploadService;
import com.example.fileUpload.util.ExternalFileMap;
import com.example.fileUpload.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.Tika;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.example.fileUpload.util.DirectoryChecker.generateFolder;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileUploadServiceImpl implements FileUploadService {

    private final FileDao fileDao; //mybatis
    private final OleDao oleDao; //mybatis
    private final FileProcessor fileProcessor;
    private final FileEncryptService fileEncryptService;

    private final StringBuffer stringBuffer = new StringBuffer();

    @Value("${Save-Directory}")
    private String baseDir;

    @SneakyThrows
    @Override
    @Transactional
    public synchronized void fileUpload(FileDto fileDto) {
        try {

            //코드의 순서와 로직을 조금 바꿔서 적절하게 재구축 해보기
            validateFileDto(fileDto);

            fileDto.getFileData().transferTo(new File(fileDto.getFileSavePath()));

            if (!this.fileDao.saveFile(fileDto)) {
                throw new FileUploadException();
            }

            this.fileProcessor.createOleExtractorHandler(fileDto);

            processExternalFiles(fileDto);

            this.fileEncryptService.encryptFile(fileDto);

        } catch (IOException | RuntimeException e) {
            ExceptionUtils.getStackTrace(e);

            stringBuffer.append(this.baseDir).append(File.separator).append(fileDto.getUUIDFileName());
            Files.delete(Path.of(stringBuffer.toString()));

           throw new FileUploadException("Failed to upload the File.");

        }finally {
            stringBuffer.delete(0, stringBuffer.length());
        }
    }



/*    public synchronized void fileUpload(FileDto fileDto) {


        try {

            if (fileDto.getFileData().isEmpty() || !FileUtil.isPathValidForStorage(this.baseDir, fileDto.getFileSavePath())
            ||!FileUtil.validateUploadedFileMimeType(fileDto)) {

                throw new FileUploadException();
            }


            generateFolder(fileDto.getFileOlePath());

            fileDto.getFileData().transferTo(new File(fileDto.getFileSavePath()));


            if(!this.fileDao.saveFile(fileDto)){
                throw new FileUploadException();
            }



            this.fileProcessor.createOleExtractorHandler(fileDto);

            ExternalFileMap.forEach(entry -> {
                OleDto oleDto = OleDto.builder()
                        .superId(fileDto.getId())
                        .originalFileName(entry.getKey())
                        .UUIDFileName(entry.getValue())
                        .build();
                this.oleDao.insertOle(oleDto);
            });

            ExternalFileMap.resetMap();

            if (fileDto.isEncrypt()) {
                this.fileEncryptService.encryptFile(fileDto);
            }


        } catch (IOException | RuntimeException e) {
            ExceptionUtils.getStackTrace(e);

            stringBuffer.append(this.baseDir).append(File.separator).append(fileDto.getUUIDFileName());
            Files.delete(Path.of(stringBuffer.toString()));

            stringBuffer.delete(0, stringBuffer.length());

        }
    }*/

    /*public synchronized boolean fileUpload(FileDto fileDto) {

        try {
            if (!fileDto.getFileData().isEmpty()) {

                if (FileUtil.isPathValidForStorage(this.baseDir, fileDto.getFileSavePath())) {

                    if (FileUtil.validateUploadedFileMimeType(fileDto)) {
                        //System.out.println(dir+File.separator+"temp"+File.separator+fileDto.getUUIDFileName());

                        fileDto.getFileData().transferTo(new File(fileDto.getFileSavePath()));


                        boolean fileResult = this.fileDao.saveFile(fileDto);

                        if(!Files.exists(Path.of(fileDto.getFileOlePath()))){
                            try{
                                //Folder.mkdir(); //폴더 생성합니다.
                                Files.createDirectories(Path.of(fileDto.getFileOlePath()));
                            }
                            catch(Exception e){
                                ExceptionUtils.getStackTrace(e);
                            }
                        }


                        this.fileProcessor.createOleExtractorHandler(fileDto);
                        ExternalFileMap.forEach(entry -> {

                            OleDto oleDto = OleDto.builder().superId(fileDto.getId())
                                    .originalFileName(entry.getKey())
                                    .UUIDFileName(entry.getValue())
                                    .build();



                            this.oleDao.insertOle(oleDto);

                        });
                        ExternalFileMap.resetMap();

                        if(fileDto.isEncrypt()){
                            this.fileEncryptService.encryptFile(fileDto);

                        }

                        return fileResult;
                        //return true;
                    }
                }
            }
        } catch (RuntimeException e) {
            ExceptionUtils.getStackTrace(e);

            new File(this.baseDir+File.separator + fileDto.getUUIDFileName()).delete();

        } catch (IOException i) {
            ExceptionUtils.getStackTrace(i);
        }
        return false;
    }*/

    @Override
    public synchronized List<FileVO> printFileAll() {
        return this.fileDao.printFileAll();
    }

    @Override
    public synchronized FileVO printFileOne(Long id) {

        Optional<FileVO> optionalFileVO = Optional.ofNullable(this.fileDao.printFileOne(id));

        return optionalFileVO.orElse(null);
    }

    @Override
    public synchronized List<OleVO> printOleAll(Long id) {

        return this.oleDao.selectById(id);
    }

    @Override
    public synchronized boolean deleteOne(Long id) {
        boolean fileResult;
        boolean oleResult;

        FileVO fileVO = this.fileDao.printFileOne(id);
        if (fileVO == null) {
            return false;
        }
        try{
            //Path filePath = Path.of(this.baseDir, fileVO.getUUIDFileName());
            if (!Files.exists(Path.of(fileVO.getFileSavePath())) || !FileUtil.isPathValidForStorage(this.baseDir, fileVO.getFileSavePath())) {
                return false;
            }

            Files.delete(Path.of(fileVO.getFileSavePath()));
            fileResult = this.fileDao.deleteById(fileVO.getId());

            Files.delete(Path.of(fileVO.getFileOlePath()));
            oleResult = this.oleDao.deleteById(fileVO.getId());

            /*//File folder = new File(fileVO.getFileOlePath());
            Files.delete(Path.of(fileVO.getFileOlePath()));

            *//*FileUtil.deleteFolder(fileVO.getFileOlePath());
            folder.delete();*//*

            oleResult = this.oleDao.deleteById(fileVO.getId());*/


        }catch (IOException e){
            ExceptionUtils.getStackTrace(e);
            return false;
        }
        /*stringBuffer.append(this.baseDir).append(File.separator).append(fileVO.getUUIDFileName());
        if (!FileUtil.isPathValidForStorage(this.baseDir, stringBuffer.toString())) {
            return false;
        }
        stringBuffer.delete(0, stringBuffer.length());

        stringBuffer.append(this.baseDir).append(File.separator).append(fileVO.getUUIDFileName());


        File file = new File(stringBuffer.toString());
        //Files.exists(stringBuffer.toString(), );
        stringBuffer.delete(0, stringBuffer.length());


        if (!(file.exists() && file.delete())) {
            log.warn(fileVO.getUUIDFileName() + " 파일 삭제 오류 발생 또는 파일이 없음, DB에서 정보 삭제");
            //return true;
        }
        File folder = new File(fileVO.getFileOlePath());

        boolean fileResult = this.fileDao.deleteById(fileVO.getId());

        FileUtil.deleteFolder(folder);
        folder.delete();

        boolean oleResult = this.oleDao.deleteById(fileVO.getId());*/

        return fileResult && oleResult;
    }

    private void validateFileDto(FileDto fileDto) throws FileUploadException {
        if (fileDto.getFileData().isEmpty() || !FileUtil.isPathValidForStorage(this.baseDir, fileDto.getFileSavePath())
               /* || !FileUtil.validateUploadedFileMimeType(fileDto)*/) {
            throw new FileUploadException();
        }
    }

    private void processExternalFiles(FileDto fileDto) {
        ExternalFileMap.forEach(entry -> {
            OleDto oleDto = OleDto.builder()
                    .superId(fileDto.getId())
                    .originalFileName(entry.getKey())
                    .UUIDFileName(entry.getValue())
                    .build();
            this.oleDao.insertOle(oleDto);
        });
        ExternalFileMap.resetMap();
    }


}