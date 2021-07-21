package com.fms.service.storage;

import com.fms.model.StorageStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileStorageStrategyFactory {

    private final StorageStrategy storageStrategy;

    private final FilePerDateStorageStrategyService filePerDateStorageStrategyService;
    private final FilePerYearDateStorageStrategyService filePerYearDateStorageStrategyService;
    private final FilePerYearMonthDateStorageStrategyService filePerYearMonthDateStorageStrategyService;
    private final FilePerYearMonthDayStorageStrategyService filePerYearMonthDayStorageStrategyService;
    private final FilePerYearMonthStorageStrategyService filePerYearMonthStorageStrategyService;
    private final FileStorageStrategyService fileStorageStrategyService;

    @Autowired
    public FileStorageStrategyFactory(@Value("${storage.strategy}") StorageStrategy storageStrategy,
                                      FilePerDateStorageStrategyService filePerDateStorageStrategyService,
                                      FilePerYearDateStorageStrategyService filePerYearDateStorageStrategyService,
                                      FilePerYearMonthDateStorageStrategyService filePerYearMonthDateStorageStrategyService,
                                      FilePerYearMonthDayStorageStrategyService filePerYearMonthDayStorageStrategyService,
                                      FilePerYearMonthStorageStrategyService filePerYearMonthStorageStrategyService,
                                      FileStorageStrategyService fileStorageStrategyService
    ) {
        this.storageStrategy = storageStrategy;
        this.filePerDateStorageStrategyService = filePerDateStorageStrategyService;
        this.filePerYearDateStorageStrategyService = filePerYearDateStorageStrategyService;
        this.filePerYearMonthDateStorageStrategyService = filePerYearMonthDateStorageStrategyService;
        this.filePerYearMonthDayStorageStrategyService = filePerYearMonthDayStorageStrategyService;
        this.filePerYearMonthStorageStrategyService = filePerYearMonthStorageStrategyService;
        this.fileStorageStrategyService = fileStorageStrategyService;
    }

    public StorageStrategyService getStorageStrategyMode() {

        switch (storageStrategy) {
            case FILE:
                return fileStorageStrategyService;
            case FILE_PER_DATE:
                return filePerDateStorageStrategyService;
            case FILE_PER_YEAR_DATE:
                return filePerYearDateStorageStrategyService;
            case FILE_PER_YEAR_MONTH:
                return filePerYearMonthStorageStrategyService;
            case FILE_PER_YEAR_MONTH_DAY:
                return filePerYearMonthDayStorageStrategyService;
            case FILE_PER_YEAR_MONTH_DATE:
                return filePerYearMonthDateStorageStrategyService;

            default:
                throw new RuntimeException("No strategy was found!");
        }

    }

}
