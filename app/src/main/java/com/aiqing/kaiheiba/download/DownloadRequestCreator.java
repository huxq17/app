package com.aiqing.kaiheiba.download;


import java.io.File;

public class DownloadRequestCreator {
    private DownloadInfo downloadInfo;
    private String url;
    private String path;
    private int threadNum = 2;
    private String group;
    private String avatar;
    private String downloadName;

    private DownloadManager downloadManager;

    public DownloadRequestCreator(String url, DownloadManager downloadManager) {
        this.url = url;
        this.downloadManager = downloadManager;
    }

    public DownloadRequestCreator threadNum(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    public DownloadRequestCreator group(String group) {
        this.group = group;
        return this;
    }

    public DownloadRequestCreator avatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public DownloadRequestCreator downloadName(String downloadName) {
        this.downloadName = downloadName;
        return this;
    }

    public void saveTo(String path, String fileName) {
        DownloadInfo downloadInfo = new DownloadInfo(url, group, avatar, downloadName, path.concat(File.separator), fileName, threadNum);
        downloadManager.add(downloadInfo);
    }

}
