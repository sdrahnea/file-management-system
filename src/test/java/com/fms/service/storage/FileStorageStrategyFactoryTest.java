package com.fms.service.storage;

import com.fms.config.AppConfig;
import com.fms.model.StorageStrategy;
import com.fms.repository.FileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class FileStorageStrategyFactoryTest {

    @Mock
    FileRepository fileRepository;

    @Mock
    AppConfig appConfig;

    FilePerDateStorageStrategyService filePerDateStorageStrategyService = new FilePerDateStorageStrategyService(
            appConfig, fileRepository
    );

    FilePerYearDateStorageStrategyService filePerYearDateStorageStrategyService = new FilePerYearDateStorageStrategyService(
            appConfig, fileRepository
    );

    FilePerYearMonthDateStorageStrategyService filePerYearMonthDateStorageStrategyService = new FilePerYearMonthDateStorageStrategyService(
            appConfig, fileRepository
    );

    FilePerYearMonthDayStorageStrategyService filePerYearMonthDayStorageStrategyService = new FilePerYearMonthDayStorageStrategyService(
            appConfig, fileRepository
    );

    FilePerYearMonthStorageStrategyService filePerYearMonthStorageStrategyService = new FilePerYearMonthStorageStrategyService(
            appConfig, fileRepository
    );

    FileStorageStrategyService fileStorageStrategyService = new FileStorageStrategyService(
        appConfig, fileRepository
    );

    @Test
    public void should_return_file_storage_strategy_service_class() {
        FileStorageStrategyFactory fileStorageStrategyFactory = init(StorageStrategy.FILE);
        StorageStrategyService storageStrategyService = fileStorageStrategyFactory.getStorageStrategyMode();

        Assertions.assertEquals(FileStorageStrategyService.class, storageStrategyService.getClass());
    }

    @Test
    public void should_return_file_per_date_storage_strategy_service_class() {
        FileStorageStrategyFactory fileStorageStrategyFactory = init(StorageStrategy.FILE_PER_DATE);
        StorageStrategyService storageStrategyService = fileStorageStrategyFactory.getStorageStrategyMode();

        Assertions.assertEquals(FilePerDateStorageStrategyService.class, storageStrategyService.getClass());
    }

    @Test
    public void should_return_file_per_year_date_storage_strategy_service_class() {
        FileStorageStrategyFactory fileStorageStrategyFactory = init(StorageStrategy.FILE_PER_YEAR_DATE);
        StorageStrategyService storageStrategyService = fileStorageStrategyFactory.getStorageStrategyMode();

        Assertions.assertEquals(FilePerYearDateStorageStrategyService.class, storageStrategyService.getClass());
    }

    @Test
    public void should_return_file_per_year_month_date_storage_strategy_service_class() {
        FileStorageStrategyFactory fileStorageStrategyFactory = init(StorageStrategy.FILE_PER_YEAR_MONTH_DATE);
        StorageStrategyService storageStrategyService = fileStorageStrategyFactory.getStorageStrategyMode();

        Assertions.assertEquals(FilePerYearMonthDateStorageStrategyService.class, storageStrategyService.getClass());
    }

    @Test
    public void should_return_file_per_year_month_day_storage_strategy_service_class() {
        FileStorageStrategyFactory fileStorageStrategyFactory = init(StorageStrategy.FILE_PER_YEAR_MONTH_DAY);
        StorageStrategyService storageStrategyService = fileStorageStrategyFactory.getStorageStrategyMode();

        Assertions.assertEquals(FilePerYearMonthDayStorageStrategyService.class, storageStrategyService.getClass());
    }

    @Test
    public void should_return_file_per_year_month_storage_strategy_service_class() {
        FileStorageStrategyFactory fileStorageStrategyFactory = init(StorageStrategy.FILE_PER_YEAR_MONTH);
        StorageStrategyService storageStrategyService = fileStorageStrategyFactory.getStorageStrategyMode();

        Assertions.assertEquals(FilePerYearMonthStorageStrategyService.class, storageStrategyService.getClass());
    }

    private FileStorageStrategyFactory init(final StorageStrategy storageStrategy){
        return new FileStorageStrategyFactory(storageStrategy,
                filePerDateStorageStrategyService,
                filePerYearDateStorageStrategyService,
                filePerYearMonthDateStorageStrategyService,
                filePerYearMonthDayStorageStrategyService,
                filePerYearMonthStorageStrategyService,
                fileStorageStrategyService
        );
    }

}
