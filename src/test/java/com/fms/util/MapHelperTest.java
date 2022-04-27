package com.fms.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MapHelperTest {

    private static final String FILE_ID = "0123456789";
    private static final String FILE_PATH = "/opt/java/file.jar";

    @Test
    public void should_create_a_map(){
        Map<String, String> map = MapHelper.create(FILE_ID, FILE_PATH);

        Assertions.assertEquals(FILE_ID, map.get("FILE_ID"));
        Assertions.assertEquals(FILE_PATH, map.get("FILE_PATH"));
    }

}
