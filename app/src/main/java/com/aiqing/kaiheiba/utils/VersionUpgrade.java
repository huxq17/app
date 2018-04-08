package com.aiqing.kaiheiba.utils;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.VersionApi;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.huxq17.xprefs.XPrefs;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class VersionUpgrade {
    private long lastAlertTime;
    private Activity activity;

    public long getLastAlertTime() {
        return lastAlertTime;
    }

    public void setLastAlertTime() {
        lastAlertTime = System.currentTimeMillis();
        XPrefs.saveAll(VersionUpgrade.this);
    }

    public void resetLastAlertTime() {
        lastAlertTime = -1;
        XPrefs.saveAll(VersionUpgrade.this);
    }

    public VersionUpgrade() {
    }

    public VersionUpgrade(Activity activity) {
        this.activity = activity;
    }

    public boolean toastNoUpgrade;

    public VersionUpgrade setToast() {
        toastNoUpgrade = true;
        resetLastAlertTime();
        return this;
    }

    public void check() {
        ApiManager.INSTANCE.getApi(VersionApi.class)
                .getAppVersion()
                .compose(RxSchedulers.<VersionApi.Bean>compose())
                .subscribe(new BaseObserver<VersionApi.Bean.DataBean>() {
                    @Override
                    protected void onSuccess(VersionApi.Bean.DataBean dataBean) {
                        VersionApi.Bean.DataBean.AndroidBean androidBean = dataBean.getAndroid();
                        String version = androidBean.getVersion();
                        String localVersion = Utils.getAppVersionName(activity);
                        if (version.compareTo(localVersion) > 0) {
                            lastAlertTime = XPrefs.get(VersionUpgrade.class).getLastAlertTime();
                            long curTime = System.currentTimeMillis();
                            if (lastAlertTime < 0 || curTime - lastAlertTime > 24 * 60 * 60 * 1000) {
                                alert(androidBean);
                            }
                        } else {
                            if (toastNoUpgrade) {
                                Toast.makeText(activity, "已经是最新版本", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void alert(VersionApi.Bean.DataBean.AndroidBean androidBean) {
        final String url = androidBean.getUrl();
        final String version = androidBean.getVersion();
        final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("更新到" + version + "版本？")
                .setMessage("解决了若干bug并且进行了体验上的优化。")
                .setCancelable(false)
                .setNegativeButton("下次再说", null)
                .setPositiveButton("更新", null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positionButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Apk.canInstallApk(activity)) {
                            setLastAlertTime();
                            downloadApk(url);
                            alertDialog.dismiss();
                        } else {
                            Apk.openSetting(activity, -1);
                        }
                    }
                });
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setLastAlertTime();
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void downloadApk(String url) {
        File d = FileManager.getDownloadPath();
        String path = d.getAbsolutePath().concat("/").concat("");
        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS, "kaiheiba.apk");
        request.setAllowedOverMetered(true);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        File downloadPath = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath().concat("/").concat("kaiheiba.apk"));
        if (downloadPath.exists()) downloadPath.delete();
        long id = downloadManager.enqueue(request);
    }
}
