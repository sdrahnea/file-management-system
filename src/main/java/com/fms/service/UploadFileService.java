package com.fms.service;

import com.fms.config.AppConfig;
import com.fms.model.CreateFileResponseDto;
import com.fms.model.FileEntity;
import com.fms.model.StorageDto;
import com.fms.repository.FileRepository;
import com.fms.service.storage.FileStorageStrategyFactory;
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
import java.util.Map;

/**
 * This class contains implementation related to file upload methods
 */
@Slf4j
@Service
public class UploadFileService {

    private final AppConfig appConfig;
    private final FileRepository fileRepository;
    private final FileStorageStrategyFactory fileStorageStrategyFactory;

    @Autowired
    public UploadFileService(AppConfig appConfig,
                             FileRepository fileRepository,
                             FileStorageStrategyFactory fileStorageStrategyFactory) {
        this.appConfig = appConfig;
        this.fileRepository = fileRepository;
        this.fileStorageStrategyFactory = fileStorageStrategyFactory;
    }

    public CreateFileResponseDto upload(MultipartFile multipartFile, String tenant) {

        log.info("Receive multipart file request for tenant id: {}", tenant);

        StorageDto storageDto = new StorageDto();
        storageDto.setTenant(tenant);
        storageDto.setMultipartFile(multipartFile);

        Map<String, String> map = fileStorageStrategyFactory.getStorageStrategyMode().store(storageDto);

        return new CreateFileResponseDto(map.get("FILE_ID"), tenant, map.get("FILE_PATH"), Instant.now());
    }

    public CreateFileResponseDto uploadFileByTenantAndId(MultipartFile multipartFile, String tenant, String fileId) {

        log.info("Receive multipart file request for tenant id: {}, file id: {}", tenant, fileId);

        StorageDto storageDto = new StorageDto();
        storageDto.setTenant(tenant);
        storageDto.setFileId(fileId);
        storageDto.setMultipartFile(multipartFile);

        Map<String, String> map = fileStorageStrategyFactory.getStorageStrategyMode().store(storageDto);

        return new CreateFileResponseDto(map.get("FILE_ID"), tenant, map.get("FILE_PATH"), Instant.now());
    }

    public String uploadNewFile(MultipartFile multipartFile, String tenant, String fileId) {
        log.info("Receive multipart file request for tenant id: {}, file id: {}", tenant, fileId);

        return uploadFileByTenantAndId(multipartFile, tenant, fileId).getUuid();
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

        return result.getFileId();
    }

    public String upload(ByteArrayResource multipartFile, String fileId, String tenant) {
        log.info("Receive byteArrayResource request for file id: {}", fileId);

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, fileId, tenant);
        FileEntity result = saveDocument(fileId, filePath, directoryName, tenant);

        saveFile(multipartFile, filePath);

        return result.getFileId();
    }

    public String upload(ByteArrayResource multipartFile, String fileId, String directoryName, String tenant) {
        log.info("Receive byteArrayResource request for file id: {}", fileId);

        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, fileId, tenant);
        FileEntity result = saveDocument(fileId, filePath, directoryName, tenant);

        saveFile(multipartFile, filePath);

        return result.getFileId();
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
                                    final String tenant) {
        FileEntity fileEntity = new FileEntity(
                fileId, directoryName, filePath, tenant
        );

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
