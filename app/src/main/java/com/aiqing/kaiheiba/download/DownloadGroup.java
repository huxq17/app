package com.aiqing.kaiheiba.download;


public class DownloadGroup {
    public String name;
    public String url;
    public String avatar;
    public long length;
    public int progress;
    public String downloadName;
    public String filePath;

    public DownloadGroup(String name, String avatar, String downloadName, String url, long length, int progress, String filePath) {
        this.name = name;
        this.url = url;
        this.length = length;
        this.progress = progress;
        this.avatar = avatar;
        this.downloadName = downloadName;
        this.filePath = filePath;
    }

    public DownloadGroup(String name, String avatar, String gameName, String url, String filePath, int progress) {
        this.name = name;
        this.url = url;
        this.downloadName = gameName;
        this.filePath = filePath;
        this.avatar = avatar;
        this.progress = progress;
    }

    public void getUrlsByName(String name) {

    }
}
