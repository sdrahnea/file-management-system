package com.fms.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileUtilsTest {

    @Test
    public void should_correct_paths(){
        Assertions.assertEquals(
                null, FileUtils.computeAbsolutePath()
        );

        Assertions.assertEquals(
                "aaa", FileUtils.computeAbsolutePath("aaa")
        );

        Assertions.assertEquals(
                "aaa/bbb", FileUtils.computeAbsolutePath("aaa", "bbb")
        );

        Assertions.assertEquals(
                "aaa/bbb/ccc", FileUtils.computeAbsolutePath("aaa", "bbb", "ccc")
        );
    }

}
