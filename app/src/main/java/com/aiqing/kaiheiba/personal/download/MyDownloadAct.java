package com.aiqing.kaiheiba.personal.download;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.aiqing.kaiheiba.App;
import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.common.BaseRecyclerViewAdapter;
import com.aiqing.kaiheiba.decoration.RecyclerViewDivider;
import com.aiqing.kaiheiba.download.DBService;
import com.aiqing.kaiheiba.download.DownloadGroup;
import com.aiqing.kaiheiba.utils.Apk;
import com.aiqing.kaiheiba.utils.DensityUtil;
import com.aiqing.kaiheiba.utils.FileManager;
import com.aiqing.kaiheiba.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MyDownloadAct extends BaseActivity {
    RecyclerView mDownloadRV;
    DownloadListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_download);
        setTitle("我的下载");
        mDownloadRV = findViewById(R.id.download_list);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        mDownloadRV.setLayoutManager(layoutmanager);
        mAdapter = new DownloadListAdapter(this);
        mDownloadRV.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL,
                DensityUtil.dip2px(this, 10), getResources().getColor(R.color.bg_content)));
        mDownloadRV.setAdapter(mAdapter);
        mockData();
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DownloadItemBean itemBean = mAdapter.getData(position);

                int progress = itemBean.progress;
                if (progress == 100) {
                    Apk.with(MyDownloadAct.this)
                            .from(itemBean.filePath)
                            .authority("com.aiqing.kaiheiba.provider")
                            .install();
                }
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Apk.INSTANCE.onActivityResult(requestCode, resultCode, data);
    }

    public void register() {
        getWindow().getDecorView().postDelayed(queryRunnable, 300);
    }

    public void unregister() {
        getWindow().getDecorView().removeCallbacks(queryRunnable);
    }

    public Runnable queryRunnable = new Runnable() {
        @Override
        public void run() {
            mockData();
            register();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregister();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        DownloadManager.with(this).unregister(this);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public void mockData() {
        List<DownloadItemBean> beans = new ArrayList<>();
        List<DownloadGroup> list = DBService.getInstance(App.getContext()).queryDownloadList();
        DownloadManager.Query query = new DownloadManager.Query();
        for (DownloadGroup group : list) {
            int progress = group.progress;
            DownloadItemBean bean = new DownloadItemBean();
            bean.avatarUrl = group.avatar;
            bean.name = group.downloadName;
            bean.url = group.url;
            long id = Integer.parseInt(group.name);
            bean.id = id;
            bean.filePath = group.filePath;
            if (progress == 100) {
                bean.progress = 100;
                beans.add(bean);
            } else {
                Cursor cursor = ((DownloadManager) getSystemService(DOWNLOAD_SERVICE)).query(query.setFilterById(id));
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        //下载的文件到本地的目录
                        String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        //已经下载的字节数
                        long bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        //总需下载的字节数
                        long bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        //Notification 标题
                        String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                        //描述
                        String description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                        //下载对应id
                        long downloadId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                        //下载文件名称
//                    String filename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        //下载文件的URL链接
                        String url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                        group.progress = (int) (100 * bytes_downloaded / bytes_total);
//                        group.filePath = address;
                        bean.progress = group.progress;
                        beans.add(bean);
                        DBService.getInstance(App.getContext()).insertDownloadId(group);
                    }
                } finally {
                    Utils.close(cursor);
                }
            }
        }
        mAdapter.setData(beans);
    }

    public static void start(Context context, MyBusinessInfLocal myBusinessInfo) {
        download(myBusinessInfo);
        Intent intent = new Intent(context, MyDownloadAct.class);
//        intent.putExtra(DOWN, myBusinessInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void download(MyBusinessInfLocal myBusinessInfo) {
        String name = myBusinessInfo.getName();
        if (TextUtils.isEmpty(name)) {
            name = "11233123.apk";
        }
        if (!name.endsWith(".apk")) {
            name.concat(".apk");
        }
        Context context = App.getContext();
        String url = myBusinessInfo.getUrl();
        String avatar = myBusinessInfo.getIcon();
//        DownloadManager downloadManager = DownloadService.getDownloadManager(App.getContext());
        File d = FileManager.getDownloadPath();
        String path = d.getAbsolutePath().concat("/").concat(name);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, name);
        request.setAllowedOverMetered(true);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        File downloadPath = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath().concat("/").concat(name));
        if (downloadPath.exists()) downloadPath.delete();
        long id = downloadManager.enqueue(request);
        DownloadGroup downloadGroup = new DownloadGroup(id + "", avatar, name, url, downloadPath.getAbsolutePath(), 0);
        DBService.getInstance(App.getContext()).insertDownloadId(downloadGroup);
//        DownloadManager.with(App.getContext())
//                .download(url)
//                .group("1")
//                .threadNum(2)
//                .avatar("")
//                .downloadName(name)
//                .saveTo(d.getAbsolutePath(), name);
//        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    }
}
