package com.aiqing.kaiheiba.personal.download;


public class DownloadItemBean {
    public String avatarUrl;
    public String name;
    public String filePath;
    public int progress;
    public long length;
    public DownloadStatus status;
    public String url;
    public long id;

    public int getPercent() {
        return (int) (progress * 100 / length);
    }
}
