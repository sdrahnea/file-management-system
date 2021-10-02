package com.fms.service.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

public class UUIDFileIdStrategyServiceTest {

    private final static String UUID_REGEX = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})";

    UUIDFileIdStrategyService uuidFileIdStrategyService = new UUIDFileIdStrategyService();

    @Test
    public void should_return_a_value_of_a_string_class(){
        Assertions.assertEquals(
                uuidFileIdStrategyService.createId().getClass()
                , ArgumentMatchers.anyString().getClass())
        ;
    }

    @Test
    public void should_match_an_uuid_format_regex() {
        Assertions.assertEquals(true
                , uuidFileIdStrategyService.createId().matches(UUID_REGEX)
        );
    }

}
