package com.aiqing.kaiheiba.download.strategy;


import android.content.Context;

import com.aiqing.kaiheiba.download.DBService;
import com.aiqing.kaiheiba.download.DownloadInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class DefaultDownloadStrategy implements IDownloadStrategy {
    @Override
    public int download(DownloadInfo downloadInfo, int threadNum, Context context) {
        int buffSize = 8192;
        List<DownloadInfo> donelist = DBService.getInstance(context).getInfos(downloadInfo.url);
        threadNum = donelist.size() == 0 ? threadNum : donelist.size();
        if (threadNum > 1) {
            long fileLength = getFileLength(donelist, downloadInfo.url);
        }
        return 0;
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
                String value = uc.getHeaderField("content-length");
                length = Long.parseLong(value);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return length;
    }

}
