package com.aiqing.kaiheiba.download;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import static com.aiqing.kaiheiba.download.DownloadListener.DOWNLOAD_ACTION;
import static com.aiqing.kaiheiba.download.DownloadListener.DOWNLOAD_FAILED;
import static com.aiqing.kaiheiba.download.DownloadListener.DOWNLOAD_LOADING;
import static com.aiqing.kaiheiba.download.DownloadListener.DOWNLOAD_URL;

public class DownloadObserver {
    private DownloadListener listener;

    public void register(Context context, DownloadListener listener) {
        if (listener == null) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(DOWNLOAD_ACTION);
        String group = listener.getGroup();
        if (!group.isEmpty()) {
            filter.addCategory(group);
        }
        this.listener = listener;
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
    }

    public DownloadObserver() {

    }


    public void unregister(Context context) {
        if (listener == null) return;
        listener = null;
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (listener == null) return;
            if (intent.hasExtra(DOWNLOAD_LOADING)) {
                int progress = intent.getIntExtra(DOWNLOAD_LOADING, 0);
                String url = intent.getStringExtra(DOWNLOAD_URL);
                listener.onLoading(progress, url);
            } else if (intent.hasExtra(DOWNLOAD_FAILED)) {
                String msg = intent.getStringExtra(DOWNLOAD_FAILED);
                listener.onFailed(msg);
            }
        }
    };
}
