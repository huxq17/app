package com.aiqing.kaiheiba.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by huxq17 on 2016/7/8.
 */
public class SdCardUtils {

    public static boolean isCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File getExternalDir(Context context, String dirName) {
        String cacheDir = "/Android/data/system_log/";
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + cacheDir + dirName + "/");
    }

    public static File getInternalDir(Context context) {
        String dirName = "/device/";
        return new File(context.getFilesDir().getAbsolutePath() + dirName);
    }

    public static File getInternalFile(Context context) {
        File dir = getInternalDir(context);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File devicesFile = new File(dir, "identity.android");
        return devicesFile;
    }

    public static File getExternalFile(Context context) {
        File dir = getExternalDir(context, "device");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File devicesFile = new File(dir, "identity.android");
        return devicesFile;
    }

    public static String readId(Context context) {
        File deviceFile = getInternalFile(context);
        if (!deviceFile.exists()) {
            deviceFile = getExternalFile(context);
        }
        if (!deviceFile.exists()) {
            return "";
        }
        return readIdFromFile(deviceFile);
    }

    private static String readIdFromFile(File deviceFile) {
        char[] buffer = new char[1024];
        int length = -1;
        StringBuilder stringBuilder = new StringBuilder();
        FileInputStream inputStream = null;
        InputStreamReader reader = null;
        try {
            inputStream = new FileInputStream(deviceFile);
            reader = new InputStreamReader(inputStream);
            while ((length = reader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(reader);
            close(inputStream);
        }
        return stringBuilder.toString();
    }

    public static void writeId(Context context, String id) {
        File internalFile = getInternalFile(context);
        File externalFile = getExternalFile(context);

        writeId2File(id, internalFile);
        writeId2File(id, externalFile);
    }

    private static void writeId2File(String id, File deviceFile) {
        FileOutputStream fos = null;
        try {
            if (deviceFile.isDirectory()) {
                Utils.deleteFileSafely(deviceFile);
            }
            if (!deviceFile.exists()) {
                boolean result = deviceFile.createNewFile();
            }
            fos = new FileOutputStream(deviceFile);
            fos.write(id.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fos);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }
}
