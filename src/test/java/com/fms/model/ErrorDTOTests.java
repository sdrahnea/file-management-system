package com.fms.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ErrorDTOTests {

    private static final String code = "code";
    private static final String description = "description";
    private static final String fullStack = "fullStack";

    @Test
    public void shouldCreateErrorDTO(){
        ErrorDTO dto = new ErrorDTO(code, description, fullStack);

        Assertions.assertEquals(code, dto.getCode());
        Assertions.assertEquals(description, dto.getDescription());
        Assertions.assertEquals(fullStack, dto.getFullStack());
    }

}
