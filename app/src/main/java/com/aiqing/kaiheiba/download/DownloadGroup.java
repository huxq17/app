package com.aiqing.kaiheiba.download;


public class DownloadGroup {
    public String name;
    public String url;
    public String avatar;
    public long length;
    public int progress;
    public String downloadName;

    public DownloadGroup(String name, String avatar, String downloadName, String url, long length, int progress) {
        this.name = name;
        this.url = url;
        this.length = length;
        this.progress = progress;
        this.avatar = avatar;
        this.downloadName = downloadName;
    }

    public void getUrlsByName(String name) {

    }
}
