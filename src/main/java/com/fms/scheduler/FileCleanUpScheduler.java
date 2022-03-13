package com.fms.scheduler;

import com.fms.model.FileCleanUpAgeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileCleanUpScheduler {

    private Integer fileCleanUpAge;
    private FileCleanUpAgeType fileCleanUpAgeType;

    public FileCleanUpScheduler(@Value("${file.cleanup.age}") Integer fileCleanUpAge,
                                @Value("${file.cleanup.age.type}") FileCleanUpAgeType fileCleanUpAgeType){
        this.fileCleanUpAge = fileCleanUpAge;
        this.fileCleanUpAgeType = fileCleanUpAgeType;
    }

}
