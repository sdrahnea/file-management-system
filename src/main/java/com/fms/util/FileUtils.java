package com.fms.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * FileUtils class contains common methods which are used to reduce code duplication
 */
public class FileUtils {

    private final static String SLASH_SEPARATOR = "/";

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

    /**
     * Create an absolute path
     * @param args list of strings separate by coma
     * @return if only one parameter then return that element. If more than one parameter
     *      then create pair of strings based on SLASH_SEPARATOR and parameter value.
     *      If none parameter then return null value.
     */
    public static String computeAbsolutePath(String... args) {
        String absolutePath = null;
        final int ARGS_LENGTH = args.length;

        if (ARGS_LENGTH > 0) {
            absolutePath = args[0];
        }

        if (ARGS_LENGTH > 1) {
            for(int index = 1; index < ARGS_LENGTH; index++) {
                absolutePath += SLASH_SEPARATOR + args[index];
            }
        }

        return absolutePath;
    }

}
