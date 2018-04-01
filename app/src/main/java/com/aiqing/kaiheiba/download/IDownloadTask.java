package com.aiqing.kaiheiba.download;


import android.content.Context;

import com.aiqing.kaiheiba.download.downloader.NetworkDownloader;

public interface IDownloadTask {
    Context getContext();

    NetworkDownloader getDownloader();

    DownloadInfo getDownloadInfo();

    void onReady(int blockSize);

    void onBlockUpdate(int length);

    void onBlockFinish();
}
