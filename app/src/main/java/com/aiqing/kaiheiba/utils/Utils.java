package com.aiqing.kaiheiba.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Utils {
    public static Drawable setStrokenBg(int strokenWidth, int round, int strokenColor, int background) {
        GradientDrawable gd = new GradientDrawable();//创建drawable
        if (background != 0) {
            gd.setColor(background);
        }
        if (round > 0) {
            gd.setCornerRadius(round);
        }
        if (strokenWidth > 0) {
            gd.setStroke(strokenWidth, strokenColor);
        }
        return gd;
    }

    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    public static void createDirIfNotExists(String fileDir) {
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 删除文件
     */
    public static boolean delete(String filePathName) {
        if (TextUtils.isEmpty(filePathName)) return false;
        File file = new File(filePathName);
        return deleteFileSafely(file);
    }

    /**
     * 关闭流
     *
     * @param closeable closeable
     */
    public static void close(Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 重命名
     *
     * @param filePathName 原始文件路径
     * @param newPathName  新的文件路径
     * @return 是否成功
     */
    public static boolean rename(String filePathName, String newPathName) {
        if (TextUtils.isEmpty(filePathName)) return false;
        if (TextUtils.isEmpty(newPathName)) return false;

        delete(newPathName);

        File file = new File(filePathName);
        File newFile = new File(newPathName);
        if (!file.exists()) {
            return false;
        }
        File parentFile = newFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return file.renameTo(newFile);
    }

    public static String sortHashMap(LinkedHashMap hashMap) {
        Object[] key = hashMap.keySet().toArray();
        Arrays.sort(key);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < key.length; i++) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            try {
                sb.append(key[i]).append("=").append(URLDecoder.decode("" + hashMap.get(key[i]), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void install(Context context, String file) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        Uri u;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (currentApiVersion < 24) {
            u = Uri.fromFile(new File(file));
        } else {
            u = FileProvider.getUriForFile(context, "com.aiqing.kaiheiba.provider", new File(file));
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(u, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
