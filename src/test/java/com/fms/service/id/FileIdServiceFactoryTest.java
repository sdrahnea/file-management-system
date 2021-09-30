package com.fms.service.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

public class FileIdServiceFactoryTest {

    InstantFileIdStrategyService instantFileIdStrategyService = new InstantFileIdStrategyService();

    UUIDFileIdStrategyService uuidFileIdStrategyService = new UUIDFileIdStrategyService();

    @Test
    public void should_return_instant_file_id_strategy_service_instant(){
        FileIdServiceFactory fileIdServiceFactory = new FileIdServiceFactory(
                FileIdStrategy.INSTANT, instantFileIdStrategyService, uuidFileIdStrategyService
        );

        Assertions.assertEquals(fileIdServiceFactory.create().getClass(), ArgumentMatchers.anyString().getClass());
    }

    @Test
    public void should_return_instant_file_id_strategy_service_uuid(){
        FileIdServiceFactory fileIdServiceFactory = new FileIdServiceFactory(
                FileIdStrategy.UUID, instantFileIdStrategyService, uuidFileIdStrategyService
        );

        Assertions.assertEquals(fileIdServiceFactory.create().getClass(), ArgumentMatchers.anyString().getClass());
    }

}
