package com.fms.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class CreateFileResponseDTOTests {

    private final static String uuid = "uuid";
    private final static String tenant = "tenant";
    private final static String path = "path";
    private final static Instant createdAt = Instant.now();

    @Test
    public void shouldCreateFileResponseDTO(){
        CreateFileResponseDTO dto = new CreateFileResponseDTO(uuid, tenant, path, createdAt);

        Assertions.assertEquals(uuid, dto.getUuid());
        Assertions.assertEquals(tenant, dto.getTenant());
        Assertions.assertEquals(path, dto.getPath());
        org.assertj.core.api.Assertions.assertThat(dto.getCreatedAt() != null);
    }
}
