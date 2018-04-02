package com.aiqing.kaiheiba.download;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.aiqing.kaiheiba.download.downloader.NetworkDownloader;
import com.aiqing.kaiheiba.download.strategy.IDownloadStrategy;
import com.aiqing.kaiheiba.utils.Utils;
import com.andbase.tractor.task.Task;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import static com.aiqing.kaiheiba.download.DownloadListener.DOWNLOAD_ACTION;
import static com.aiqing.kaiheiba.download.DownloadListener.DOWNLOAD_URL;


public class DownloadTask extends Task implements IDownloadTask {
    private DownloadInfo mDownloadInfo;
    private Context mContext;
    private CountDownLatch mLatch;
    private IDownloadStrategy mDownloadStrategy;
    private NetworkDownloader downloader;

    public DownloadTask(final DownloadInfo info, NetworkDownloader networkDownloader, IDownloadStrategy downloadStrategy, final Context context, final Object tag) {
        super(info.url, null);
        this.mDownloadInfo = info;
        mContext = context;
        Utils.createDirIfNotExists(mDownloadInfo.fileDir);
        downloader = networkDownloader;
        mDownloadStrategy = downloadStrategy;
    }

    @Override
    public void onRun() {
        int threadNum = mDownloadInfo.threadNum;
        long completed = 0;
        //如果文件不存在就删除数据库中的缓存
        deleteCache(mDownloadInfo, mDownloadInfo.url);
        mDownloadInfo.threadNum = threadNum;
        completed = mDownloadStrategy.download(this);
        synchronized (this) {
            mDownloadInfo.computeProgress(this, completed);
        }
        if (mLatch != null) {
            try {
                mLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!mDownloadInfo.hasDownloadSuccess()) {
            notifyFail();
//            LogUtils.d("download failed! " + "size=" + size + " allocated=" + allocated + " and spendTime=" + (System.currentTimeMillis() - starttime));
        } else {
//            String group = mDownloadInfo.group;
            //删除没有group的一次性任务
//            if (group == null || group.isEmpty()) {
            DBService.getInstance(mContext).delete(mDownloadInfo.url);
//            }
            Utils.rename(mDownloadInfo.tempFilePath, mDownloadInfo.filePath);
            notifySuccess();
//            LogUtils.d("download finshed! " + "size=" + size + " allocated=" + allocated + " and spendTime=" + (System.currentTimeMillis() - starttime));
        }
    }

    @Override
    public void cancelTask() {
        for (int i = 0; i < mLatch.getCount(); i++) {
            mLatch.countDown();
        }
    }


    public void notifyUpdate(int progress) {
        DownloadGroup downloadGroup = new DownloadGroup(mDownloadInfo.group, mDownloadInfo.avatar, mDownloadInfo.downloadName,
                mDownloadInfo.url, mDownloadInfo.fileLength, mDownloadInfo.progress,mDownloadInfo.filePath);
        DBService.getInstance(mContext).insertGroup(downloadGroup);
        Intent intent = new Intent();
        intent.putExtra(DownloadListener.DOWNLOAD_LOADING, progress);
        intent.putExtra(DOWNLOAD_URL, mDownloadInfo.url);
        intent.addCategory(mDownloadInfo.group);
        notifyListener(intent);
    }

    private void notifyFail() {
//        Intent intent = new Intent();
//        intent.putExtra(DownloadListener.DOWNLOAD_FAILED, "下载失败");
//        intent.putExtra(DOWNLOAD_URL, mDownloadInfo.url);
//        intent.addCategory(mDownloadInfo.group);
//        notifyListener(intent);
    }

    private void notifyListener(Intent intent) {
        intent.setAction(DOWNLOAD_ACTION);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void notifySuccess() {
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public NetworkDownloader getDownloader() {
        return downloader;
    }

    @Override
    public DownloadInfo getDownloadInfo() {
        return mDownloadInfo;
    }

    @Override
    public void onReady(int blockSize) {
        mLatch = new CountDownLatch(blockSize);
    }

    @Override
    public void onBlockUpdate(int length) {
    }

    @Override
    public void onBlockFinish() {
        if (mLatch != null && isRunning()) {
            mLatch.countDown();
        }
    }

    private void deleteCache(DownloadInfo info, String url) {
        File file = new File(info.tempFilePath);
        if (!file.exists()) {
            DBService.getInstance(mContext).delete(url);
        }
    }

}
