package com.fms.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * FileUtils class contains common methods which are used to reduce code duplication
 */
public class FileUtils {

    /***
     * Check if directory exists. If not exists then create it.
     * @param directoryPath absolute directory path
     */
    public static void checkAndCreateDirectory(final String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    /**
     * Create full path tree of directories
     * @param location absolute directory path
     * @throws IOException
     */
    public static void checkAndCreateDirectories(final String location) throws IOException {
        FileUtils.checkAndCreateDirectory(location);
        Files.createDirectories(Paths.get(location));
    }

}
