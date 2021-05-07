package com.fms.util;

import java.io.File;

public class FileUtils {

    /***
     * Check if directory exists. If not exists then create it.
     * @param directoryPath absolute directory path
     */
    public static void checkAndCreateDirectory(final String directoryPath){
        File directory = new File(directoryPath);
        if (!directory.exists()){
            directory.mkdir();
        }
    }

}
