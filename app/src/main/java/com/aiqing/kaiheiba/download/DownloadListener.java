package com.aiqing.kaiheiba.download;

public abstract class DownloadListener {
    public static final String DOWNLOAD_ACTION = "DOWNLOAD_ACTION";
    public static final String DOWNLOAD_URL = "DOWNLOAD_URL";
    public static final String DOWNLOAD_LOADING = "DOWNLOAD_LOADING";
    public static final String DOWNLOAD_FAILED = "DOWNLOAD_FAILED";

    private String group;
    public static final String EmptyGroup = "";

    public DownloadListener(String group) {
        this.group = group;
    }

    public String getGroup() {
        if (group == null || group.isEmpty()) {
            group = EmptyGroup;
        }
        return group;
    }

    public abstract void onStart();

    public abstract void onLoading(int progress, String url);

    public abstract void onFailed(String msg);

    public abstract void onSuccess();
}
