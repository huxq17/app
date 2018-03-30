package com.aiqing.kaiheiba.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import java.io.File;

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
            if (file.exists()) {
                String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
                File tmp = new File(tmpPath);
                file.renameTo(tmp);
                return tmp.delete();
            }
        }
        return false;
    }
}
