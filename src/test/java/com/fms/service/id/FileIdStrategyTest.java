package com.fms.service.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileIdStrategyTest {

    @Test
    public void should_test_that_returns_the_right_type(){
        Assertions.assertEquals(FileIdStrategy.UUID, FileIdStrategy.valueOf("UUID"));
        Assertions.assertEquals(FileIdStrategy.INSTANT, FileIdStrategy.valueOf("INSTANT"));
    }

}
