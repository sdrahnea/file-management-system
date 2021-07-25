package com.fms.service.storage;

import com.fms.config.AppConfig;
import com.fms.model.FileEntity;
import com.fms.model.StorageDto;
import com.fms.repository.FileRepository;
import com.fms.util.DateUtils;
import com.fms.util.FileUtils;
import com.fms.util.MapHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * This class contains implementation for file storage rule:
 *          ${file.db.location} / ${tenant} / ${date} / ${file_id}
 * where:
 *  - ${file.db.location}   the main path to file storage;
 *  - ${tenant}             an generic identifier
 *  - ${date}               a folder, with yyyy-MM-dd date format
 *  - ${file_id}            an identifier given by system to content
 */

@Slf4j
@Service
public class FilePerDateStorageStrategyService implements StorageStrategyService {

    private final AppConfig appConfig;
    private final FileRepository fileRepository;

    @Autowired
    public FilePerDateStorageStrategyService(AppConfig appConfig,
                                             FileRepository fileRepository){
        this.appConfig = appConfig;
        this.fileRepository = fileRepository;
    }

    @Override
    public Map<String, String> store(StorageDto storageDto) {
        final String tenant = storageDto.getTenant();
        final MultipartFile multipartFile = storageDto.getMultipartFile();

        final String directoryName = DateUtils.getCurrentDateAsString();
        checkAndCreateDirectoryByTenant(tenant, directoryName);

        final String fileId = UUID.randomUUID().toString();

        final String filePath = computeAbsoluteFilePath(directoryName, fileId, tenant);
        saveDocument(fileId, filePath, directoryName, tenant);

        try {
            multipartFile.transferTo(new File(filePath));
        } catch (Exception exception) {
            log.error("Can not to save file: " + filePath + " exception: " + exception);
        }

        return MapHelper.create(fileId, filePath);
    }

    private FileEntity saveDocument(final String fileId,
                                    final String filePath,
                                    final String directoryName,
                                    final String tenant) {
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
            FileUtils.checkAndCreateDirectories(location);
        } catch (Exception exception) {
            log.error("Could not check or create directory: {}", exception);
        }
    }
}
