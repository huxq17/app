package com.aiqing.kaiheiba.personal.download;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aiqing.kaiheiba.App;
import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.common.BaseRecyclerViewAdapter;
import com.aiqing.kaiheiba.decoration.RecyclerViewDivider;
import com.aiqing.kaiheiba.download.DBService;
import com.aiqing.kaiheiba.download.DownloadGroup;
import com.aiqing.kaiheiba.download.DownloadListener;
import com.aiqing.kaiheiba.download.DownloadManager;
import com.aiqing.kaiheiba.utils.DensityUtil;
import com.aiqing.kaiheiba.utils.FileManager;
import com.arialyy.annotations.Download;
import com.arialyy.aria.core.download.DownloadTask;
import com.huxq17.xprefs.LogUtils;

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
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                String path = mAdapter.getData(position).getPath();
//                toast("path=" + path);
            }
        });
        mockData();
//        Aria.download(this).register();
        DownloadManager.with(this).register(listener);
    }

    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning
    protected void running(DownloadTask task) {
//        String url = task.getKey();
//        int p = task.getPercent();    //任务进度百分比
//        String speed = task.getConvertSpeed();    //转换单位后的下载速度，单位转换需要在配置文件中打开
//        DownloadEntity entity2 = task.getEntity();
//        String key = entity2.getKey();
        LogUtils.e("running");
        mockData();
    }

    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        //在这里处理任务完成的状态
        LogUtils.e("taskComplete");
        mockData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadManager.with(this).unregister(this);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void mockData() {
        List<DownloadItemBean> beans = new ArrayList<>();
        List<DownloadGroup> list = DBService.getInstance(this).getGroups("1");
        if (list == null) return;
        for (DownloadGroup entity : list) {
            DownloadItemBean bean = new DownloadItemBean();
//            bean.avatarUrl = entity.getKey();
            bean.length = entity.length;
            bean.progress = entity.progress;
            bean.avatarUrl = entity.avatar;
            bean.name = entity.downloadName;
            bean.url = entity.url;
            beans.add(bean);
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
        String name = "123123.apk";
        String url = myBusinessInfo.getUrl();
//        DownloadManager downloadManager = DownloadService.getDownloadManager(App.getContext());
        File d = FileManager.getDownloadPath();
        String path = d.getAbsolutePath().concat("/").concat(name);
//        DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(url)
//                .setPath(path)
//                .build();
//        downloadInfo.setDownloadListener(new MyDownloadListener() {
//            @Override
//            public void onRefresh() {
//                notifyRefresh();
//            }
//        });
//        downloadManager.download(downloadInfo);
//        try {
//            DBController.getInstance(App.getContext()).createOrUpdateMyDownloadInfo(myBusinessInfo);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        LogUtils.e("path=" + path + ";d.getAbsolutePath()=" + d.getAbsolutePath());
        DownloadManager.with(App.getContext())
                .download(url)
                .group("1")
                .threadNum(2)
                .avatar("")
                .downloadName(name)
                .saveTo(d.getAbsolutePath(), name);
    }

    public static final String DOWNLOAD_REFRESH = "download_refresh";

    public DownloadListener listener = new DownloadListener("1") {
        @Override
        public void onStart() {

        }

        @Override
        public void onLoading(int progress, String url) {
            mockData();
        }

        @Override
        public void onFailed(String msg) {

        }

        @Override
        public void onSuccess() {

        }
    };
}
