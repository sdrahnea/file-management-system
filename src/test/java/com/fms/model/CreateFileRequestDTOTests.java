package com.fms.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

public class CreateFileRequestDTOTests {

    @Mock
    MultipartFile multipartFile;

    @Test
    public void shouldCreateFileRequestDTO(){
        CreateFileRequestDTO dto = new CreateFileRequestDTO("xxx",  multipartFile);

        assertEquals("xxx", dto.getTenant());
        Assertions.assertThat(dto.getMultipartFile() != null);
    }

}
