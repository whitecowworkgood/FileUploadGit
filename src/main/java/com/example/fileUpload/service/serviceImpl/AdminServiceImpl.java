package com.example.fileUpload.service.serviceImpl;

import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.repository.FileEntityDAO;
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

    private final FileEntityDAO fileEntityDao;

    @Override
    public List<FileVO> printBeforeAcceptFiles() {
        return this.fileEntityDao.beforeAcceptFiles();
    }

    @Override
    public synchronized void acceptFile(Long id) {
        this.fileEntityDao.acceptFile(id);
    }
}
