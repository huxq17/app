package com.aiqing.kaiheiba.download;

import android.content.Context;

import com.aiqing.kaiheiba.download.downloader.NetworkDownloader;
import com.aiqing.kaiheiba.download.downloader.OkHttpDownloader;
import com.aiqing.kaiheiba.download.strategy.IDownloadStrategy;
import com.aiqing.kaiheiba.download.strategy.StrategyFactory;
import com.aiqing.kaiheiba.utils.Utils;

import java.io.File;
import java.util.concurrent.CountDownLatch;


public class DownloadTask implements IDownloadTask {
    private DownloadInfo mDownloadInfo;
    private Context mContext;
    private CountDownLatch mLatch;
    IDownloadStrategy mDownloadStrategy;
    private boolean mRunning = false;
    private NetworkDownloader downloader;

    public DownloadTask(final DownloadInfo info, final Context context, final Object tag) {
        this.mDownloadInfo = info;
        mContext = context;
        Utils.createDirIfNotExists(mDownloadInfo.fileDir);
        downloader = new OkHttpDownloader();
    }

    public void start() {
        mRunning = true;
        int threadNum = mDownloadInfo.threadNum;
        threadNum = threadNum < 1 ? 1 : threadNum;
        mDownloadStrategy = StrategyFactory.getDownloadStrategy(threadNum);
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
            String group = mDownloadInfo.group;
            //删除没有group的一次性任务
//            if (group == null || group.isEmpty()) {
            DBService.getInstance(mContext).delete(mDownloadInfo.url);
//            }
            Utils.rename(mDownloadInfo.tempFilePath, mDownloadInfo.filePath);
            notifySuccess();
//            LogUtils.d("download finshed! " + "size=" + size + " allocated=" + allocated + " and spendTime=" + (System.currentTimeMillis() - starttime));
        }
        mRunning = false;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void notifyUpdate(int progress) {
    }

    private void notifyFail() {

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
        if (mLatch != null && mRunning) {
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
