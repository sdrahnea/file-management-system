package com.fms.service.storage;

import com.fms.model.StorageStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileStorageStrategyFactory {

    private final StorageStrategy storageStrategy;

    private final FilePerDateStorageStrategyServiceService filePerDateStorageStrategyService;
    private final FilePerYearDateStorageStrategyServiceService filePerYearDateStorageStrategyService;
    private final FilePerYearMonthDateStorageStrategyServiceService filePerYearMonthDateStorageStrategyService;
    private final FilePerYearMonthDayStorageStrategyServiceService filePerYearMonthDayStorageStrategyService;
    private final FilePerYearMonthStorageStrategyServiceService filePerYearMonthStorageStrategyService;
    private final FileStorageStrategyServiceService fileStorageStrategyService;

    @Autowired
    public FileStorageStrategyFactory(@Value("${storage.strategy}") StorageStrategy storageStrategy,
                                      FilePerDateStorageStrategyServiceService filePerDateStorageStrategyService,
                                      FilePerYearDateStorageStrategyServiceService filePerYearDateStorageStrategyService,
                                      FilePerYearMonthDateStorageStrategyServiceService filePerYearMonthDateStorageStrategyService,
                                      FilePerYearMonthDayStorageStrategyServiceService filePerYearMonthDayStorageStrategyService,
                                      FilePerYearMonthStorageStrategyServiceService filePerYearMonthStorageStrategyService,
                                      FileStorageStrategyServiceService fileStorageStrategyService
                                      ){
        this.storageStrategy = storageStrategy;
        this.filePerDateStorageStrategyService = filePerDateStorageStrategyService;
        this.filePerYearDateStorageStrategyService = filePerYearDateStorageStrategyService;
        this.filePerYearMonthDateStorageStrategyService = filePerYearMonthDateStorageStrategyService;
        this.filePerYearMonthDayStorageStrategyService = filePerYearMonthDayStorageStrategyService;
        this.filePerYearMonthStorageStrategyService = filePerYearMonthStorageStrategyService;
        this.fileStorageStrategyService = fileStorageStrategyService;
    }

    public StorageStrategyService getStorageStrategyMode(){

        switch (storageStrategy) {
            case FILE: return fileStorageStrategyService;
            case FILE_PER_DATE: return filePerDateStorageStrategyService;
            case FILE_PER_YEAR_DATE: return filePerYearDateStorageStrategyService;
            case FILE_PER_YEAR_MONTH: return filePerYearMonthStorageStrategyService;
            case FILE_PER_YEAR_MONTH_DAY: return filePerYearMonthDayStorageStrategyService;
            case FILE_PER_YEAR_MONTH_DATE: return filePerYearMonthDateStorageStrategyService;

            default: throw new RuntimeException("No strategy was found!");
        }

    }

}
