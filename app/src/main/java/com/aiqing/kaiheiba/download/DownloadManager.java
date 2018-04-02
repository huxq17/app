package com.aiqing.kaiheiba.download;

import android.content.Context;

import com.aiqing.kaiheiba.download.downloader.NetworkDownloader;
import com.aiqing.kaiheiba.download.downloader.OkHttpDownloader;
import com.aiqing.kaiheiba.download.strategy.IDownloadStrategy;
import com.aiqing.kaiheiba.download.strategy.StrategyFactory;
import com.andbase.tractor.task.TaskPool;
import com.huxq17.xprefs.LogUtils;


public enum DownloadManager {
    INSTANCE;
    private Context context;
    private NetworkDownloader networkDownloader;
    private IDownloadStrategy downloadStrategy;
    private DownloadObserver observer;

    void init(Context context, NetworkDownloader networkDownloader, IDownloadStrategy downloadStrategy) {
        this.context = context;
        this.networkDownloader = networkDownloader;
        this.downloadStrategy = downloadStrategy;
    }

    public static DownloadManager with(Context context) {
        return new Builder(context).build();
    }

    public DownloadRequestCreator download(String url) {
        return new DownloadRequestCreator(url, this);
    }

    void add(DownloadInfo downloadInfo) {
        if (downloadStrategy == null) {
            int threadNum = downloadInfo.threadNum;
            threadNum = threadNum < 1 ? 1 : threadNum;
            downloadStrategy = StrategyFactory.getDownloadStrategy(threadNum);
        }
        DownloadTask task = new DownloadTask(downloadInfo, networkDownloader, downloadStrategy, context, null);
//        Executor.execute(task);
        TaskPool.getInstance().execute(task);
    }

    static class Builder {
        private final Context context;
        private NetworkDownloader networkDownloader;
        private IDownloadStrategy downloadStrategy;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder networkDownloader(NetworkDownloader networkDownloader) {
            this.networkDownloader = networkDownloader;
            return this;
        }

        public Builder downloadStrategy(IDownloadStrategy downloadStrategy) {
            this.downloadStrategy = downloadStrategy;
            return this;
        }

        public DownloadManager build() {
            DownloadManager downloadManager = INSTANCE;
            if (networkDownloader == null) {
                networkDownloader = new OkHttpDownloader();
            }
            downloadManager.init(context, networkDownloader, downloadStrategy);
            return downloadManager;
        }
    }

    public void register(DownloadListener listener) {
        observer = new DownloadObserver();
        observer.register(context, listener);
    }

    public void unregister(Context context) {
        if (observer != null) {
            observer.unregister(context);
        }
    }

    public void cancel(String url) {
        LogUtils.e("cancel url="+url);
        TaskPool.getInstance().cancelTask(url);
    }
}
