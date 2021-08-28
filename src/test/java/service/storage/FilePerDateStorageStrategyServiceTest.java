package service.storage;

import com.fms.config.AppConfig;
import com.fms.model.StorageDto;
import com.fms.repository.FileRepository;
import com.fms.service.storage.FilePerDateStorageStrategyService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;

import java.util.Collections;

public class FilePerDateStorageStrategyServiceTest {

    AppConfig appConfig = new AppConfig(Collections.emptyList(), "DUMMY_PATH", false);

    @Mock
    FileRepository fileRepository;

    FilePerDateStorageStrategyService filePerDateStorageStrategyService = new FilePerDateStorageStrategyService(appConfig, fileRepository);

    @Test
    public void should_run_storage(){
        StorageDto storageDto = new StorageDto();
        storageDto.setMultipartFile(ArgumentMatchers.any());
        storageDto.setTenant(ArgumentMatchers.anyString());
        storageDto.setContent(ArgumentMatchers.anyString().getBytes());

        filePerDateStorageStrategyService.store(storageDto);
    }
}
