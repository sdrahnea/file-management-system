package com.dms.service;

import com.dms.model.DocumentFile;
import com.dms.repository.DocumentFileRepository;
import com.dms.util.DateUtils;
import com.dms.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class DocumentFileService {

    private final static String ENVIRONMENT = "prod";

    private final String prodFileDbLocation;
    private final String testFileDbLocation;
    private final DocumentFileRepository documentFileRepository;

    @Autowired
    public DocumentFileService(@Value("${prod.file.db.location}") String prodFileDbLocation,
                               @Value("${test.file.db.location}") String testFileDbLocation,
                               DocumentFileRepository documentFileRepository) {
        this.prodFileDbLocation = prodFileDbLocation;
        this.testFileDbLocation = testFileDbLocation;
        this.documentFileRepository = documentFileRepository;
    }

    public String upload(MultipartFile multipartFile, String documentId, String environment) {

        log.info("Receive multipart file request for document id: {}", documentId);

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByEnv(environment, directoryName);

        final String filePath = computeAbsoluteFilePath(directoryName, documentId, environment);

        DocumentFile result = saveDocument(documentId, filePath, directoryName, environment);

        try {
            multipartFile.transferTo(new File(filePath));
        } catch (Exception exception) {
            log.error("Can not to save file: " + filePath + " exception: " + exception);
        }

        return result.getDocumentId();
    }

    public String upload(ByteArrayResource multipartFile, String documentId, String environment) {
        log.info("Receive byteArrayResource request for document id: {}", documentId);

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByEnv(environment, directoryName);

        final String filePath = computeAbsoluteFilePath(directoryName, documentId, environment);

        DocumentFile result = saveDocument(documentId, filePath, directoryName, environment);

        try {
            Files.write(Paths.get(filePath), multipartFile.getByteArray());
        } catch (Exception exception) {
            log.error("Can not to save file: {} with exception: {}", filePath, exception);
        }

        return result.getDocumentId();
    }

    public String upload(ByteArrayResource multipartFile, String documentId, String directoryName, String environment) {
        log.info("Receive byteArrayResource request for document id: {}", documentId);

        checkAndCreateDirectoryByEnv(environment, directoryName);

        final String filePath = computeAbsoluteFilePath(directoryName, documentId, environment);

        DocumentFile result = saveDocument(documentId, filePath, directoryName, environment);

        try {
            Files.write(Paths.get(filePath), multipartFile.getByteArray());
        } catch (Exception exception) {
            log.error("Can not to save file: {} with exception: {}", filePath, exception);
        }

        return result.getDocumentId();
    }

    public byte[] download(String documentId) {
        log.info("Find data for document id: {}", documentId);
        List<DocumentFile> documentFileList = documentFileRepository.findByDocumentId(documentId.trim());

        DocumentFile documentFile = null;
        if(!documentFileList.isEmpty()) {
            documentFile = documentFileList.get(0);
        } else {
            throw new RuntimeException("No record was found for document ID: {}" + documentId);
        }


        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Paths.get(documentFile.getPath()));
        } catch(Exception exception) {
            log.error("Can not to download fom this path: {}, throw exception: {}", documentFile.getPath(), exception);
        }

        return bytes;
    }

    private DocumentFile saveDocument(final String documentId,
                                      final String filePath,
                                      final String directoryName,
                                      final String environment){
        DocumentFile documentFile = new DocumentFile();
        documentFile.setDocumentId(documentId);
        documentFile.setDirectory(directoryName);
        documentFile.setPath(filePath);
        documentFile.setTenant(environment);

        log.info("Save to DB the document id: {}", documentId);

        return documentFileRepository.save(documentFile);
    }

    private String computeAbsoluteFilePath(final String directoryName, final String documentId, final String environment){
        return environment.equalsIgnoreCase(ENVIRONMENT)
                ? prodFileDbLocation + directoryName + "/" + documentId
                : testFileDbLocation + directoryName + "/" + documentId;
    }

    private void checkAndCreateDirectoryByEnv(final String environment, final String directoryName) {
        FileUtils.checkAndCreateDirectory(environment.equalsIgnoreCase(ENVIRONMENT)
                ? prodFileDbLocation + directoryName
                : testFileDbLocation + directoryName);
    }

}
