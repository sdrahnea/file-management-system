package com.fms.service;

import com.fms.exception.FileNotFoundException;
import com.fms.model.FileEntity;
import com.fms.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * This class contains implementation related to file download methods
 */
@Slf4j
@Service
public class DownloadFileService {

    private final FileRepository fileRepository;
    private final UsageService usageService;

    @Autowired
    public DownloadFileService(FileRepository fileRepository, UsageService usageService) {
        this.fileRepository = fileRepository;
        this.usageService = usageService;
    }

    public byte[] download(String fileId) {
        log.info("Find data for file id: {}", fileId);
        List<FileEntity> fileEntityList = fileRepository.findByFileId(fileId.trim());

        if (fileEntityList.isEmpty()) {
            throw new FileNotFoundException(fileId);
        }

        return readAndTrack(fileEntityList.get(0), fileId);
    }

    public byte[] downloadByTenant(String fileId, String tenant) {
        log.info("Find data for file id: {} and tenant: {}", fileId, tenant);
        List<FileEntity> fileEntityList = fileRepository.findByFileIdAndTenant(fileId.trim(), tenant);

        if (fileEntityList.isEmpty()) {
            throw new FileNotFoundException(fileId);
        }

        return readAndTrack(fileEntityList.get(0), fileId);
    }

    private byte[] readAndTrack(FileEntity fileEntity, String fileId) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(fileEntity.getPath()));
            usageService.recordDownload(
                    fileEntity.getTenant(),
                    fileEntity.getFileId(),
                    fileEntity.getFileSizeBytes() == null ? bytes.length : fileEntity.getFileSizeBytes()
            );
            return bytes;
        } catch (Exception exception) {
            log.error("Can not download from path: {}", fileEntity.getPath(), exception);
            throw new RuntimeException("Failed to read file content for id: " + fileId);
        }
    }
}
