package com.fms.service;

import com.fms.config.AppConfig;
import com.fms.model.CreateFileResponseDTO;
import com.fms.model.FileEntity;
import com.fms.model.StorageDTO;
import com.fms.repository.FileRepository;
import com.fms.service.storage.FileStorageStrategyFactory;
import com.fms.util.DateUtils;
import com.fms.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
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
    private final QuotaService quotaService;
    private final UsageService usageService;

    @Autowired
    public UploadFileService(AppConfig appConfig,
                             FileRepository fileRepository,
                             FileStorageStrategyFactory fileStorageStrategyFactory,
                             QuotaService quotaService,
                             UsageService usageService) {
        this.appConfig = appConfig;
        this.fileRepository = fileRepository;
        this.fileStorageStrategyFactory = fileStorageStrategyFactory;
        this.quotaService = quotaService;
        this.usageService = usageService;
    }

    @Transactional
    public CreateFileResponseDTO upload(MultipartFile multipartFile, String tenant) {
        log.info("Receive multipart file request for tenant id: {}", tenant);
        quotaService.assertUploadAllowed(tenant, multipartFile.getSize());

        StorageDTO storageDto = new StorageDTO();
        storageDto.setTenant(tenant);
        storageDto.setMultipartFile(multipartFile);

        Map<String, String> map = fileStorageStrategyFactory.getStorageStrategyMode().store(storageDto);
        String fileId = map.get("FILE_ID");
        String filePath = map.get("FILE_PATH");

        enrichMetadata(fileId, tenant, multipartFile.getSize(), multipartFile.getContentType(), checksumSafe(multipartFile));
        usageService.recordUpload(tenant, fileId, multipartFile.getSize());

        return new CreateFileResponseDTO(fileId, tenant, filePath, Instant.now());
    }

    @Transactional
    public CreateFileResponseDTO uploadFileByTenantAndId(MultipartFile multipartFile, String tenant, String fileId) {
        log.info("Receive multipart file request for tenant id: {}, file id: {}", tenant, fileId);
        quotaService.assertUploadAllowed(tenant, multipartFile.getSize());

        StorageDTO storageDto = new StorageDTO();
        storageDto.setTenant(tenant);
        storageDto.setFileId(fileId);
        storageDto.setMultipartFile(multipartFile);

        Map<String, String> map = fileStorageStrategyFactory.getStorageStrategyMode().store(storageDto);
        String resolvedFileId = map.get("FILE_ID");
        String filePath = map.get("FILE_PATH");

        enrichMetadata(resolvedFileId, tenant, multipartFile.getSize(), multipartFile.getContentType(), checksumSafe(multipartFile));
        usageService.recordUpload(tenant, resolvedFileId, multipartFile.getSize());

        return new CreateFileResponseDTO(resolvedFileId, tenant, filePath, Instant.now());
    }

    @Transactional
    public String uploadNewFile(MultipartFile multipartFile, String tenant, String fileId) {
        log.info("Receive multipart file request for tenant id: {}, file id: {}", tenant, fileId);
        return uploadFileByTenantAndId(multipartFile, tenant, fileId).getUuid();
    }

    @Transactional
    public String upload(MultipartFile multipartFile, String fileId, String tenant) {
        log.info("Receive multipart file request for file id: {}", fileId);
        quotaService.assertUploadAllowed(tenant, multipartFile.getSize());

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, fileId, tenant);
        FileEntity result = saveDocument(fileId, filePath, directoryName, tenant,
                multipartFile.getSize(), multipartFile.getContentType(), checksumSafe(multipartFile));

        try {
            multipartFile.transferTo(new File(filePath));
        } catch (Exception exception) {
            log.error("Can not save file: {}", filePath, exception);
            throw new RuntimeException("Failed to save file content for id: " + fileId);
        }

        usageService.recordUpload(tenant, fileId, multipartFile.getSize());
        return result.getFileId();
    }

    @Transactional
    public String upload(ByteArrayResource multipartFile, String fileId, String tenant) {
        log.info("Receive byteArrayResource request for file id: {}", fileId);
        byte[] content = multipartFile.getByteArray();
        quotaService.assertUploadAllowed(tenant, content.length);

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, fileId, tenant);
        FileEntity result = saveDocument(fileId, filePath, directoryName, tenant,
                (long) content.length, "application/octet-stream", checksumSafe(content));

        saveFile(multipartFile, filePath);
        usageService.recordUpload(tenant, fileId, content.length);

        return result.getFileId();
    }

    @Transactional
    public String upload(ByteArrayResource multipartFile, String fileId, String directoryName, String tenant) {
        log.info("Receive byteArrayResource request for file id: {}", fileId);
        byte[] content = multipartFile.getByteArray();
        quotaService.assertUploadAllowed(tenant, content.length);

        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, fileId, tenant);
        FileEntity result = saveDocument(fileId, filePath, directoryName, tenant,
                (long) content.length, "application/octet-stream", checksumSafe(content));

        saveFile(multipartFile, filePath);
        usageService.recordUpload(tenant, fileId, content.length);

        return result.getFileId();
    }

    private void saveFile(ByteArrayResource multipartFile, final String filePath) {
        try {
            Files.write(Paths.get(filePath), multipartFile.getByteArray());
        } catch (Exception exception) {
            log.error("Can not save file: {}", filePath, exception);
            throw new RuntimeException("Failed to save byte-array file content");
        }
    }

    private FileEntity saveDocument(final String fileId,
                                    final String filePath,
                                    final String directoryName,
                                    final String tenant,
                                    final Long fileSizeBytes,
                                    final String contentType,
                                    final String checksum) {
        FileEntity fileEntity = new FileEntity(fileId, directoryName, filePath, tenant);
        fileEntity.setFileSizeBytes(fileSizeBytes);
        fileEntity.setContentType(contentType);
        fileEntity.setChecksum(checksum);

        log.info("Save to database the file id: {}", fileId);
        return fileRepository.save(fileEntity);
    }

    private void enrichMetadata(String fileId,
                                String tenant,
                                long fileSizeBytes,
                                String contentType,
                                String checksum) {
        List<FileEntity> entityList = fileRepository.findByFileIdAndTenant(fileId, tenant);
        if (entityList.isEmpty()) {
            return;
        }
        FileEntity entity = entityList.get(0);
        entity.setFileSizeBytes(fileSizeBytes);
        entity.setContentType(contentType);
        entity.setChecksum(checksum);
        fileRepository.save(entity);
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
            log.error("Could not check or create directory", exception);
            throw new RuntimeException("Failed to prepare destination directory");
        }
    }

    private String checksumSafe(MultipartFile multipartFile) {
        try {
            return checksumSafe(multipartFile.getBytes());
        } catch (Exception exception) {
            log.warn("Could not compute checksum for multipart file", exception);
            return null;
        }
    }

    private String checksumSafe(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(content);
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception exception) {
            log.warn("Could not compute checksum for content", exception);
            return null;
        }
    }
}
