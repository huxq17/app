package cn.woblog.android.downloader.db;

import java.util.List;

import cn.woblog.android.downloader.domain.DownloadInfo;
import cn.woblog.android.downloader.domain.DownloadThreadInfo;

/**
 * Created by renpingqing on 17/1/23.
 */

public interface DownloadDBController {

    List<DownloadInfo> findAll();

    List<DownloadInfo> findAllDownloading();

    List<DownloadInfo> findAllDownloaded();

    DownloadInfo findDownloadedInfoById(int id);

    void pauseAllDownloading();

    void createOrUpdate(DownloadInfo downloadInfo);

    void createOrUpdate(DownloadThreadInfo downloadThreadInfo);

    void delete(DownloadInfo downloadInfo);

    void delete(DownloadThreadInfo download);
}
