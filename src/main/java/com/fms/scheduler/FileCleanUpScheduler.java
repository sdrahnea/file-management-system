package com.fms.scheduler;

import com.fms.model.FileCleanUpAgeType;
import com.fms.model.FileEntity;
import com.fms.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Scheduler starts every five minutes and scan the file directory. Found all file which meets
 *  the condition and remove them.
 */

@Slf4j
@Service
public class FileCleanUpScheduler {

    private final Integer fileCleanUpAge;
    private final FileCleanUpAgeType fileCleanUpAgeType;
    private final FileRepository fileRepository;

    public FileCleanUpScheduler(@Value("${file.cleanup.age}") Integer fileCleanUpAge,
                                @Value("${file.cleanup.age.type}") FileCleanUpAgeType fileCleanUpAgeType,
                                FileRepository fileRepository) {
        this.fileCleanUpAge = fileCleanUpAge;
        this.fileCleanUpAgeType = fileCleanUpAgeType;
        this.fileRepository = fileRepository;
    }

    // Runs every 5 minutes.
    @Scheduled(cron = "0 */5 * * * ?")
    public void cleanFilesByAge() {
        log.info("Start clean-up by age. age={}, type={}", fileCleanUpAge, fileCleanUpAgeType);

        Date cutoffDate = computeCutoffDate();
        List<FileEntity> filesToBeRemoved = fileRepository.getFileForDeleting(cutoffDate);

        int removed = 0;
        for (FileEntity entity : filesToBeRemoved) {
            try {
                if (entity.getPath() != null) {
                    Files.deleteIfExists(Paths.get(entity.getPath()));
                }
                fileRepository.deleteById(entity.getId());
                removed++;
                log.info("Removed file id={} tenant={} path={}", entity.getFileId(), entity.getTenant(), entity.getPath());
            } catch (Exception exception) {
                log.error("Failed to remove file id={} path={}", entity.getFileId(), entity.getPath(), exception);
            }
        }

        log.info("Stop clean-up by age. Candidates={}, Removed={}", filesToBeRemoved.size(), removed);
    }

    private Date computeCutoffDate() {
        Instant now = Instant.now();
        switch (fileCleanUpAgeType) {
            case DAY:
                return Date.from(now.minus(fileCleanUpAge, ChronoUnit.DAYS));
            case MONTH:
                return Date.from(now.minus(fileCleanUpAge * 30L, ChronoUnit.DAYS));
            case YEAR:
            case YAER:
                return Date.from(now.minus(fileCleanUpAge * 365L, ChronoUnit.DAYS));
            default:
                return Date.from(now.minus(fileCleanUpAge, ChronoUnit.DAYS));
        }
    }
}
