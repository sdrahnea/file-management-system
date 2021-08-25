package service.storage;

import com.fms.config.AppConfig;
import com.fms.model.StorageDto;
import com.fms.repository.FileRepository;
import com.fms.service.storage.FileStorageStrategyService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;

import java.util.Collections;

public class FileStorageStrategyServiceTest {

    AppConfig appConfig = new AppConfig(Collections.emptyList(), "DUMMY_PATH", false);

    @Mock
    FileRepository fileRepository;

    FileStorageStrategyService fileStorageStrategyService = new FileStorageStrategyService(appConfig, fileRepository);

    @Test
    public void should_run_storage(){
        StorageDto storageDto = new StorageDto();
        storageDto.setMultipartFile(ArgumentMatchers.any());
        storageDto.setTenant(ArgumentMatchers.anyString());
        storageDto.setContent(ArgumentMatchers.anyString().getBytes());

        fileStorageStrategyService.store(storageDto);
    }
}
