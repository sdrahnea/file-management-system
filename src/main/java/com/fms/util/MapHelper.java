package com.fms.util;

import java.util.HashMap;
import java.util.Map;

/**
 * MapHelper class contains methods to reduce code duplication
 */
public class MapHelper {

    /**
     * Create a map with two keys: FILE_ID and FILE_PATH
     * @param fileId    generated file ID value
     * @param filePath  generated file path value
     * @return a map which contains FILE_ID and FILE_PATH elements
     */
    public static Map<String, String> create(final String fileId,
                                             final String filePath){
        Map<String, String> map = new HashMap<>();
        map.put("FILE_ID", fileId);
        map.put("FILE_PATH", filePath);

        return map;
    }

}
