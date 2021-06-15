package com.fms.service;

import com.fms.config.AppConfig;
import com.fms.model.CreateFileResponseDto;
import com.fms.model.FileEntity;
import com.fms.repository.FileRepository;
import com.fms.util.DateUtils;
import com.fms.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private final AppConfig appConfig;
    private final FileRepository fileRepository;

    @Autowired
    public FileService(AppConfig appConfig, FileRepository fileRepository) {
        this.appConfig = appConfig;
        this.fileRepository = fileRepository;
    }

    public CreateFileResponseDto upload(MultipartFile multipartFile, String tenant) {

        log.info("Receive multipart file request for tenant id: {}", tenant);

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByTenant(tenant, directoryName);

        final String fileId = UUID.randomUUID().toString();

        final String filePath = computeAbsoluteFilePath(directoryName, fileId, tenant);
        FileEntity result = saveDocument(fileId, filePath, directoryName, tenant);

        try {
            multipartFile.transferTo(new File(filePath));
        } catch (Exception exception) {
            log.error("Can not to save file: " + filePath + " exception: " + exception);
        }

        return new CreateFileResponseDto(fileId, tenant, Instant.now());
    }

    public String upload(MultipartFile multipartFile, String fileId, String tenant) {

        log.info("Receive multipart file request for file id: {}", fileId);

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, fileId, tenant);
        FileEntity result = saveDocument(fileId, filePath, directoryName, tenant);

        try {
            multipartFile.transferTo(new File(filePath));
        } catch (Exception exception) {
            log.error("Can not to save file: " + filePath + " exception: " + exception);
        }

        return result.getDocumentId();
    }

    public String upload(ByteArrayResource multipartFile, String fileId, String tenant) {
        log.info("Receive byteArrayResource request for file id: {}", fileId);

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, fileId, tenant);
        FileEntity result = saveDocument(fileId, filePath, directoryName, tenant);

        saveFile(multipartFile, filePath);

        return result.getDocumentId();
    }

    public String upload(ByteArrayResource multipartFile, String fileId, String directoryName, String tenant) {
        log.info("Receive byteArrayResource request for file id: {}", fileId);

        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, fileId, tenant);
        FileEntity result = saveDocument(fileId, filePath, directoryName, tenant);

        saveFile(multipartFile, filePath);

        return result.getDocumentId();
    }

    public byte[] download(String fileId) {
        log.info("Find data for file id: {}", fileId);
        List<FileEntity> fileEntityList = fileRepository.findByDocumentId(fileId.trim());

        FileEntity fileEntity;
        if(!fileEntityList.isEmpty()) {
            fileEntity = fileEntityList.get(0);
        } else {
            throw new RuntimeException("No record was found for file ID: {}" + fileId);
        }

        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Paths.get(fileEntity.getPath()));
        } catch(Exception exception) {
            log.error("Can not to download fom this path: {}, throw exception: {}", fileEntity.getPath(), exception);
        }

        return bytes;
    }

    private void saveFile(ByteArrayResource multipartFile, final String filePath) {
        try {
            Files.write(Paths.get(filePath), multipartFile.getByteArray());
        } catch (Exception exception) {
            log.error("Can not to save file: {} with exception: {}", filePath, exception);
        }
    }

    private FileEntity saveDocument(final String fileId,
                                    final String filePath,
                                    final String directoryName,
                                    final String tenant){
        FileEntity fileEntity = new FileEntity();
        fileEntity.setDocumentId(fileId);
        fileEntity.setDirectory(directoryName);
        fileEntity.setPath(filePath);
        fileEntity.setTenant(tenant);

        log.info("Save to database the file id: {}", fileId);

        return fileRepository.save(fileEntity);
    }

    private String computeAbsoluteFilePath(final String directoryName, final String fileId, final String tenant) {
        return appConfig.getFileDbLocation() + "/" + tenant + "/" + directoryName + "/" + fileId;
    }

    private void checkAndCreateDirectoryByTenant(final String tenant, final String directoryName) {
        try {
            final String location = appConfig.getFileDbLocation() + "/" + tenant + "/" + directoryName;
            FileUtils.checkAndCreateDirectory(location);
            Files.createDirectories(Paths.get(location));
        } catch (Exception exception) {
            log.error("Could not check or create directory: {}", exception);
        }
    }

}
