package com.aiqing.kaiheiba.personal.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aiqing.kaiheiba.download.DBService;

public class DownloadProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//        String action = intent.getAction();
        DBService.getInstance(context).updateDownloadId(id + "", 100);
    }
}
