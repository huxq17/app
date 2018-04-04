package com.aiqing.kaiheiba.utils;


import android.app.Activity;
import android.content.Intent;

public enum Apk {
    INSTANCE;
    private String uri;
    private ApkInstaller apkInstaller;

    public static ApkInstaller with(Activity context) {
        return ApkInstaller.INSTANCE.init(INSTANCE, context);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ApkInstaller.INSTANCE.onActivityResult(requestCode, resultCode, data);
    }
}
