package com.aiqing.kaiheiba.download.strategy;


import android.content.Context;

import com.aiqing.kaiheiba.download.DBService;
import com.aiqing.kaiheiba.download.DownloadException;
import com.aiqing.kaiheiba.download.DownloadGroup;
import com.aiqing.kaiheiba.download.DownloadInfo;
import com.aiqing.kaiheiba.download.DownloadTask;
import com.aiqing.kaiheiba.download.Executor;
import com.aiqing.kaiheiba.download.IDownloadTask;
import com.aiqing.kaiheiba.download.downloader.NetworkDownloader;
import com.aiqing.kaiheiba.utils.Utils;
import com.huxq17.xprefs.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class DefaultDownloadStrategy implements IDownloadStrategy {
    private Context context;
    private IDownloadTask downloadTask;

    @Override
    public int download(IDownloadTask downloadTask) {
        int completed = 0;
        this.downloadTask = downloadTask;
        context = downloadTask.getContext();
        int mRunningThreadNum = 0;
        DownloadInfo downloadInfo = downloadTask.getDownloadInfo();
        int threadNum = downloadInfo.threadNum;
        List<DownloadInfo> donelist = DBService.getInstance(context).getInfos(downloadInfo.url);
        threadNum = donelist.size() == 0 ? threadNum : donelist.size();
        if (threadNum > 1) {
            long fileLength = getFileLength(donelist, downloadInfo.url);
            if (fileLength <= 0) {
                throw new DownloadException("获取不到下载文件大小");
            }
            downloadInfo.fileLength = fileLength;
            long block = fileLength % threadNum == 0 ? fileLength / threadNum : fileLength / threadNum + 1;
            for (int i = 0; i < threadNum; i++) {
                final long startPos = i * block;
                final long endPos = (i + 1) * block - 1;
                downloadInfo.startPos = startPos;
                downloadInfo.endPos = endPos;
                if (i < donelist.size()) {
                    DownloadInfo blockInfo = donelist.get(i);
                    downloadInfo.downloadId = blockInfo.downloadId;
                    completed += blockInfo.startPos - downloadInfo.startPos;
                    downloadInfo.startPos = blockInfo.startPos;
                    downloadInfo.endPos = blockInfo.endPos;
                } else {
                    downloadInfo.downloadId = i;
                }
                if (downloadInfo.downloadId == threadNum - 1) {
                    downloadInfo.endPos = fileLength;
                }
                if (downloadInfo.startPos < downloadInfo.endPos) {
                    mRunningThreadNum++;
                    LogUtils.d("startMultiThread start=" + downloadInfo.startPos + ";end=" + downloadInfo.endPos + ";filelength=" + fileLength
                            + ";info.downloadId=" + downloadInfo.downloadId);
                    downBlock(downloadInfo);
                }
            }
            if (mRunningThreadNum > 0) {
                DownloadGroup downloadGroup = new DownloadGroup(downloadInfo.group,downloadInfo.avatar,downloadInfo.downloadName, downloadInfo.url, fileLength, downloadInfo.progress);
                DBService.getInstance(context).insertGroup(downloadGroup);
                downloadTask.onReady(mRunningThreadNum);
            }
        }
        return completed;
    }

    private void downBlock(final DownloadInfo info) {
        final long startPos = info.startPos;
        final long endPos = info.endPos;
        final String url = info.url;
        final String filepath = info.tempFilePath;
        final int downloadId = info.downloadId;
        Executor.execute(new Runnable() {
            @Override
            public void run() {
                long curPos = startPos;
                long realEndPos = endPos;
                String range = null;
                RandomAccessFile accessFile = null;
                if (startPos != -1 && realEndPos != -1) {
                    range = "bytes=" + startPos + "-" + realEndPos;
                }
                InputStream inputStream = null;
                try {
                    NetworkDownloader.Response response = downloadTask.getDownloader().load(url, range);
                    inputStream = response.inputStream;
                    if (inputStream != null) {
                        File saveFile = new File(filepath);
                        accessFile = new RandomAccessFile(saveFile, "rwd");
                        if (startPos < 0) {
                            info.fileLength = response.contentLength;
                            accessFile.setLength(info.fileLength);
                            curPos = 0;
                            realEndPos = info.fileLength;
                            accessFile.seek(curPos);
                        } else {
                            accessFile.seek(startPos);
                        }
                        final int buffSize = 8192;
                        byte[] buffer = new byte[buffSize];
                        int len = 0;
                        while (curPos <= realEndPos) {
                            len = inputStream.read(buffer);
                            if (len == -1) {
//                            notifyDownloadFailed(null);
                                break;
                            }
                            if (curPos + len > realEndPos) {
                                len = (int) (realEndPos - curPos + 1);//获取正确读取的字节数
                            }
                            synchronized (downloadTask) {
                                if (!info.computeProgress((DownloadTask) downloadTask, len)) {
                                    //当下载任务失败以后结束此下载线程
                                    break;
                                }
                                curPos += len;
                                accessFile.write(buffer, 0, len);
                            }
                        }
                    }
                } catch (IOException e) {
                    LogUtils.e("download block ioe");
                    e.printStackTrace();
                } finally {
                    if (!info.hasDownloadSuccess()) {
                        DBService.getInstance(context).updataInfos(downloadId, curPos, realEndPos, url);
                    }
                    Utils.close(inputStream);
                    Utils.close(accessFile);
                    downloadTask.onBlockFinish();
                }
            }
        });
    }

    private long getFileLength(List<DownloadInfo> donelist, String downloadUrl) {
        long length = -1;
        for (DownloadInfo info : donelist) {
            if (info.endPos > length) {
                length = info.endPos;
            }
        }
        if (length <= 0) {
            try {
                URL url = new URL(downloadUrl);
                URLConnection uc = url.openConnection();
                uc.connect();
                length = uc.getHeaderFieldInt("content-length", -1);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return length;
    }

}
