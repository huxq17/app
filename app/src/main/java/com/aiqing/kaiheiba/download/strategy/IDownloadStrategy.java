package com.aiqing.kaiheiba.download.strategy;

import android.content.Context;

import com.aiqing.kaiheiba.download.DownloadInfo;

public interface IDownloadStrategy {
    int download(DownloadInfo info, int ThreadNum, Context context);
}
