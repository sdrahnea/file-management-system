package com.fms.service.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

public class InstantFileIdStrategyServiceTest {

    private final static String INTEGER_REGEX = "^\\d+$";

    InstantFileIdStrategyService instantFileIdStrategyService = new InstantFileIdStrategyService();

    @Test
    public void should_return_a_value_of_a_string_class(){
        Assertions.assertEquals(
                instantFileIdStrategyService.createId().getClass()
                , ArgumentMatchers.anyString().getClass())
        ;

        Assertions.assertEquals(true
                , instantFileIdStrategyService.createId().matches(INTEGER_REGEX)
        );
    }

    @Test
    public void should_match_an_integer_format_regex() {
        Assertions.assertEquals(true
                , instantFileIdStrategyService.createId().matches(INTEGER_REGEX)
        );
    }

}
