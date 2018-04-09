package com.aiqing.kaiheiba.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;

public enum ApkInstaller {
    INSTANCE;
    private Apk apk;
    private String path;
    private String uri;
    private WeakReference<Context> contextWeakRef;
    // use for android N
    private String authority;


    public ApkInstaller init(Apk apk, Context context) {
        this.apk = apk;
        contextWeakRef = new WeakReference<>(context);
        return this;
    }

    public ApkInstaller from(String path) {
        this.path = path;
        return this;
    }

    public ApkInstaller fromUri(String path) {
        this.uri = path;
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

    public boolean canInstallApk() {
        return Apk.canInstallApk(contextWeakRef.get());
    }

    private void installApk() {
        if(contextWeakRef==null)return;
        Context context = contextWeakRef.get();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        Uri u;
        if (currentApiVersion < 24) {
            if (!TextUtils.isEmpty(uri)) {
                u = Uri.parse(uri);
            } else {
                u = Uri.fromFile(new File(path));
            }
        } else {
            if (TextUtils.isEmpty(authority)) {
                throw new RuntimeException("authority is isEmpty");
            }
            File file = null;
            if (!TextUtils.isEmpty(uri)) {
                try {
                    file = new File(new URI(uri));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                file = new File(path);
            }
            if (file == null) return;
            u = FileProvider.getUriForFile(context, authority, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(u, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void openSetting() {
        Apk.openSetting(contextWeakRef.get(), GET_UNKNOWN_APP_SOURCES);
    }

}
