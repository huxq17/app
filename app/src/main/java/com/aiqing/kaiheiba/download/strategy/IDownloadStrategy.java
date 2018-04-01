package com.aiqing.kaiheiba.download.strategy;

import com.aiqing.kaiheiba.download.IDownloadTask;

public interface IDownloadStrategy {
    int download(IDownloadTask downLoadTask);
}
