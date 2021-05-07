package com.fms.service;

import com.fms.config.AppConfig;
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
import java.util.List;

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

    public String upload(MultipartFile multipartFile, String documentId, String tenant) {

        log.info("Receive multipart file request for document id: {}", documentId);

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, documentId, tenant);
        FileEntity result = saveDocument(documentId, filePath, directoryName, tenant);

        try {
            multipartFile.transferTo(new File(filePath));
        } catch (Exception exception) {
            log.error("Can not to save file: " + filePath + " exception: " + exception);
        }

        return result.getDocumentId();
    }

    public String upload(ByteArrayResource multipartFile, String documentId, String tenant) {
        log.info("Receive byteArrayResource request for document id: {}", documentId);

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, documentId, tenant);
        FileEntity result = saveDocument(documentId, filePath, directoryName, tenant);

        try {
            Files.write(Paths.get(filePath), multipartFile.getByteArray());
        } catch (Exception exception) {
            log.error("Can not to save file: {} with exception: {}", filePath, exception);
        }

        return result.getDocumentId();
    }

    public String upload(ByteArrayResource multipartFile, String documentId, String directoryName, String tenant) {
        log.info("Receive byteArrayResource request for document id: {}", documentId);

        checkAndCreateDirectoryByTenant(tenant, directoryName);
        final String filePath = computeAbsoluteFilePath(directoryName, documentId, tenant);
        FileEntity result = saveDocument(documentId, filePath, directoryName, tenant);

        try {
            Files.write(Paths.get(filePath), multipartFile.getByteArray());
        } catch (Exception exception) {
            log.error("Can not to save file: {} with exception: {}", filePath, exception);
        }

        return result.getDocumentId();
    }

    public byte[] download(String documentId) {
        log.info("Find data for document id: {}", documentId);
        List<FileEntity> fileEntityList = fileRepository.findByDocumentId(documentId.trim());

        FileEntity fileEntity = null;
        if(!fileEntityList.isEmpty()) {
            fileEntity = fileEntityList.get(0);
        } else {
            throw new RuntimeException("No record was found for document ID: {}" + documentId);
        }

        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Paths.get(fileEntity.getPath()));
        } catch(Exception exception) {
            log.error("Can not to download fom this path: {}, throw exception: {}", fileEntity.getPath(), exception);
        }

        return bytes;
    }

    private FileEntity saveDocument(final String documentId,
                                    final String filePath,
                                    final String directoryName,
                                    final String tenant){
        FileEntity fileEntity = new FileEntity();
        fileEntity.setDocumentId(documentId);
        fileEntity.setDirectory(directoryName);
        fileEntity.setPath(filePath);
        fileEntity.setTenant(tenant);

        log.info("Save to DB the document id: {}", documentId);

        return fileRepository.save(fileEntity);
    }

    private String computeAbsoluteFilePath(final String directoryName, final String documentId, final String tenant) {
        return appConfig.getFileDbLocation() + "/" + tenant + "/" + directoryName + "/" + documentId;
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
