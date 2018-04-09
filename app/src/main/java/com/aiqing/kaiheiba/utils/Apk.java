package com.aiqing.kaiheiba.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

public enum Apk {
    INSTANCE;
    private String uri;
    private ApkInstaller apkInstaller;

    public static ApkInstaller with(Context context) {
        return ApkInstaller.INSTANCE.init(INSTANCE, context);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code) {
            ApkInstaller.INSTANCE.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static boolean canInstallApk(Context context) {
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

    int code = -2;

    public static void openSetting(Context context, int code) {
        code = code;
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
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
        if (activity == null || code < 0) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, code);
        }
    }
}
