package com.fms.service.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

public class InstantFileIdStrategyServiceTest {

    private final static String INTEGER_REGEX = "^\\d+$";

    InstantFileIdStrategyService instantFileIdStrategyService = new InstantFileIdStrategyService();

    @Test
    public void should_return_instant_value(){
        Assertions.assertEquals(
                instantFileIdStrategyService.createId().getClass()
                , ArgumentMatchers.anyString().getClass())
        ;

        Assertions.assertEquals(true
                , instantFileIdStrategyService.createId().matches(INTEGER_REGEX)
        );
    }

}
