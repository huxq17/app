package com.aiqing.kaiheiba.download;


import com.huxq17.xprefs.LogUtils;

/**
 * 记载下载器详细信息的类
 */
public class DownloadInfo {
    public String group = "";//处于group里的下载任务会一直存在，除非手动去删除
    public int downloadId;//下载器id
    public long startPos = -1;//开始点
    public long endPos = -1;//结束点
    public long completeSize;//完成度
    public String url;//下载器网络标识
    public long fileLength;
    public String filePath;//文件保存路径
    public String fileDir;//保存文件的文件夹路径
    public String filename;//保存的文件名
    public String tempFilePath;
    public int threadNum;
    public int progress;
    public String avatar;
    public String downloadName;

    public DownloadInfo(String url, String group, String avatar, String downloadName, String fileDir, String filename, int threadNum) {
        this.url = url;
        this.fileDir = fileDir;
        this.filename = filename;
        this.group = group;
        this.filePath = this.fileDir + this.filename;
        this.threadNum = threadNum;
        this.tempFilePath = Utils.getTempPath(filePath);
        this.avatar = avatar;
        this.downloadName = downloadName;
    }

    public DownloadInfo(int downloadId,
                        long startPosition, long endPosition, String url) {
        this.downloadId = downloadId;
        this.startPos = startPosition;
        this.endPos = endPosition;
        this.url = url;
    }

    /**
     * 计算进度，并通知ui更新
     *
     * @param done
     */
    public boolean computeProgress(DownloadTask downloadTask, long done) {
//        LogUtils.d("completeSize=" + completeSize + ";done=" + done+";task.ismRunning="+task.ismRunning());
        if (downloadTask == null || !downloadTask.isRunning()) {
            //当下载任务不在运行时，返回false，主要用于其他下载子线程停止下载动作
            return false;
        }
        completeSize += done;
        int progress = (int) (100 * (1.0f * completeSize / fileLength));
        if (progress != this.progress) {
            this.progress = progress;
            downloadTask.notifyUpdate(progress);
        }
//        if (hasDownloadSuccess()) {
////            synchronized (task) {
//                task.notify();
////            }
//        }
        return true;
    }

    public boolean hasDownloadSuccess() {
        LogUtils.d("hasDownloadSuccess completeSize=" + completeSize + ";fileLength=" + fileLength);
        return completeSize >= fileLength;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "startPos=" + startPos +
                ", endPos=" + endPos +
                ", compeleteSize=" + completeSize +
                ", url='" + url + '\'' +
                ", fileLength=" + fileLength +
                ", filePath='" + filePath + '\'' +
                ", fileDir='" + fileDir + '\'' +
                ", filename='" + filename + '\'' +
                ", threadNum=" + threadNum +
                '}';
    }

}