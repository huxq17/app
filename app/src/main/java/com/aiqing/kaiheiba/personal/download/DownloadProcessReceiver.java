package com.aiqing.kaiheiba.personal.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.aiqing.kaiheiba.download.DBService;
import com.aiqing.kaiheiba.utils.Apk;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//        String action = intent.getAction();
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
//                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    DBService.getInstance(context).updateDownloadId(id + "", 100);
                    String address = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    Apk.with(context)
                            .fromUri(address)
                            .authority("com.aiqing.kaiheiba.provider")
                            .install();
//                    if (address.endsWith("kaiheiba.apk") ) {
//                        Apk.with(context)
//                                .from(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath().concat("/").concat("kaiheiba.apk"))
//                                .authority("com.aiqing.kaiheiba.provider")
//                                .install();
//                    }
                } else if (status == DownloadManager.STATUS_FAILED) {
                    MyBusinessInfLocal info = DBService.getInstance(context).queryDownloadById(id + "");
                    downloadManager.remove(id);
                    MyDownloadAct.download(info);
                }
            }
        } finally {
            com.aiqing.kaiheiba.utils.Utils.close(c);
        }
    }
}
