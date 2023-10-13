package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.repository.FileDao;
import com.example.fileUpload.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final FileDao fileDao;

    @Override
    public List<FileVO> printBeforeAcceptFiles() {

        return this.fileDao.beforeAcceptFiles();
    }

    @Override
    public synchronized void acceptFile(Long id) {
        this.fileDao.acceptFile(id);
    }
}
