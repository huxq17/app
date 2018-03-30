package com.aiqing.kaiheiba.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;


public class FileManager {
    public static final String PATH = "kaiheiba";

    public static File getSDPath(Context context, String dirName) {
        String cacheDir = "/kaiheiba/";
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + cacheDir + dirName + "/");
    }

    public static File getDownloadPath() {
        String cacheDir = "/kaiheiba/download";
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + cacheDir + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
