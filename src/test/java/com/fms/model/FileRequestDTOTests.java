package com.fms.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileRequestDTOTests {

    private static final String fileId = "fileId";
    private static final String tenant = "tenant";

    @Test
    public void shouldCreateFileRequestDTO(){
        FileRequestDTO dto = new FileRequestDTO(fileId, tenant);

        Assertions.assertEquals(fileId, dto.getFileId());
        Assertions.assertEquals(tenant, dto.getTenant());
    }

}
