package com.fms.service.id;

import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * This class contains implementation for file id strategy, in case of INSTANT file id strategy value.
 */
@Service
public class InstantFileIdStrategyService implements FileIdStrategyService {

    @Override
    public String createId() {
        return "" + Instant.now().toEpochMilli();
    }

}
