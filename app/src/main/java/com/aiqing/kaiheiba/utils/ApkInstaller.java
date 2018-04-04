package com.aiqing.kaiheiba.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

public enum ApkInstaller {
    INSTANCE;
    private Apk apk;
    private String uri;
    private WeakReference<Activity> contextWeakRef;
    // use for android N
    private String authority;

    public ApkInstaller init(Apk apk, Activity context) {
        this.apk = apk;
        contextWeakRef = new WeakReference<>(context);
        return this;
    }

    public ApkInstaller from(String uri) {
        this.uri = uri;
        return this;
    }

    public ApkInstaller authority(String authority) {
        this.authority = authority;
        return this;
    }

    public void install() {
        if (canInstallApk()) {
            installApk();
        } else {
            openSetting();
        }
    }

    private static final int GET_UNKNOWN_APP_SOURCES = 1;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_UNKNOWN_APP_SOURCES:
                if (resultCode == Activity.RESULT_OK) {
                    installApk();
                } else {
//                    Context context = contextWeakRef.get();
//                    Toast.makeText(context, "用户未授权", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    private boolean canInstallApk() {
        Context context = contextWeakRef.get();
        if (Build.VERSION.SDK_INT >= 26) {
            return context.getPackageManager().canRequestPackageInstalls();
        } else {
            int id = 0;
            try {
                id = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (id == 1) {
                return true;
            }
        }
        return false;
    }

    private void installApk() {
        Context context = contextWeakRef.get();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        Uri u;
        if (currentApiVersion < 24) {
            u = Uri.fromFile(new File(uri));
        } else {
            if (TextUtils.isEmpty(authority)) {
                throw new RuntimeException("authority is isEmpty");
            }
            u = FileProvider.getUriForFile(context, authority, new File(uri));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(u, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void openSetting() {
        Activity context = contextWeakRef.get();
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            Toast.makeText(context, "请允许安装应用", Toast.LENGTH_SHORT).show();
            Uri packageURI = Uri.parse("package:" + context.getPackageName());
            intent.setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(packageURI);
        } else {
            Toast.makeText(context, "请勾选从外部来源安装应用", Toast.LENGTH_SHORT).show();
            intent.setAction(android.provider.Settings.ACTION_SECURITY_SETTINGS);
        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
    }
}
