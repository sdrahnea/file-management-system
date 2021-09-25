package com.fms.service.id;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * This class contains implementation for file id strategy, in case of UUID file id strategy value.
 */
@Service
public class UUIDFileIdStrategyService implements FileIdStrategyService {

    @Override
    public String createId() {
        return UUID.randomUUID().toString();
    }

}
