package com.aiqing.kaiheiba.download;


public class Utils {
    private final static String TEMP_SUFFIX = ".temp";

    public static String getTempPath(String filePath) {
        return filePath.concat(TEMP_SUFFIX);
    }
}
