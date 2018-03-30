package com.aiqing.kaiheiba.personal.download;


public enum DownloadStatus {
    CANCEL("取消"), DELETE("删除");
    String des;

    DownloadStatus(String des) {
        this.des = des;
    }

    public String getDes() {
        return des;
    }
}
