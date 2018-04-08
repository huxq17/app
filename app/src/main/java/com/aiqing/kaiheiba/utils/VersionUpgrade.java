package com.aiqing.kaiheiba.utils;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

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

    public VersionUpgrade() {
    }

    public VersionUpgrade(Activity activity) {
        this.activity = activity;
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
                        }
                    }
                });
    }

    private void alert(VersionApi.Bean.DataBean.AndroidBean androidBean) {
        final String url = androidBean.getUrl();
        final String version = androidBean.getVersion();
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("更新到" + version + "版本？")
                .setMessage("解决了若干bug并且进行了体验上的优化。")
                .setCancelable(false)
                .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setLastAlertTime();
                    }
                })
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                })
                .create();
        alertDialog.show();
    }
}
