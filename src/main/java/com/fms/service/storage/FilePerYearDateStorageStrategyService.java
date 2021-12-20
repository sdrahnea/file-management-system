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
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * This class contains implementation for file storage rule:
 *          ${file.db.location} / ${tenant} / ${year} / ${date} /${file_id}
 * where:
 *  - ${file.db.location}   the main path to file storage;
 *  - ${tenant}             an generic identifier
 *  - ${year}               a folder, which represents the year
 *  - ${date}               a folder, with yyyy-MM-dd date format
 *  - ${file_id}            an identifier given by system to content
 */
@Slf4j
@Service
public class FilePerYearDateStorageStrategyService implements StorageStrategyService {

    private final AppConfig appConfig;
    private final FileRepository fileRepository;

    @Autowired
    public FilePerYearDateStorageStrategyService(AppConfig appConfig,
                                                 FileRepository fileRepository){
        this.appConfig = appConfig;
        this.fileRepository = fileRepository;
    }

    @Override
    public Map<String, String> store(StorageDto storageDto) {
        final String tenant = storageDto.getTenant();
        final MultipartFile multipartFile = storageDto.getMultipartFile();

        final String fileId = FileUtils.createIdIfNull(storageDto.getFileId());

        final String filePath = computeAbsoluteFilePath(fileId, tenant);

        try {
            FileUtils.checkAndCreateDirectories(filePath);
            saveFile(fileId, filePath, tenant);
            multipartFile.transferTo(new File(filePath));
        } catch (Exception exception) {
            log.error("Can not to save file: " + filePath + " exception: " + exception);
        }

        return MapHelper.create(fileId, filePath);
    }

    private FileEntity saveFile(final String fileId,
                                final String filePath,
                                final String tenant) {
        FileEntity fileEntity = new FileEntity(
                fileId, filePath, tenant
        );

        log.info("Save to database the file id: {}", fileId);

        return fileRepository.save(fileEntity);
    }

    private String computeAbsoluteFilePath(final String fileId, final String tenant) {
        Date date = new Date();
        return FileUtils.computeAbsolutePath(appConfig.getFileDbLocation()
                , tenant
                , DateUtils.getYear(date)
                , DateUtils.formatDate(date)
                , fileId
        );
    }

}
