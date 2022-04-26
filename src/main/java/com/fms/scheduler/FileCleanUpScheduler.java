package com.fms.scheduler;

import com.fms.model.FileCleanUpAgeType;
import com.fms.model.FileEntity;
import com.fms.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Scheduler starts every five minutes and scan the file directory. Found all file which meets
 *  the condition and remove them.
 */
@Slf4j
@Service
public class FileCleanUpScheduler {

    private Integer fileCleanUpAge;
    private FileCleanUpAgeType fileCleanUpAgeType;
    private FileRepository fileRepository;

    public FileCleanUpScheduler(@Value("${file.cleanup.age}") Integer fileCleanUpAge,
                                @Value("${file.cleanup.age.type}") FileCleanUpAgeType fileCleanUpAgeType,
                                FileRepository fileRepository){
        this.fileCleanUpAge = fileCleanUpAge;
        this.fileCleanUpAgeType = fileCleanUpAgeType;
        this.fileRepository = fileRepository;
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void cleanFilesByAge() {
        log.info("Start to clean-up files by age");


        List<FileEntity> filesToBeRemoved = fileRepository.getFileForDeleting(1);

        log.info("Found entities: {}", filesToBeRemoved.size());


        log.info("Stop to clean-up files by age");
    }

}
