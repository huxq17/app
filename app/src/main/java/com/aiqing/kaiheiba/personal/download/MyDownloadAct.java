package com.aiqing.kaiheiba.personal.download;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aiqing.kaiheiba.App;
import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.common.BaseRecyclerViewAdapter;
import com.aiqing.kaiheiba.decoration.RecyclerViewDivider;
import com.aiqing.kaiheiba.utils.DensityUtil;
import com.aiqing.kaiheiba.utils.FileManager;
import com.aiqing.kaiheiba.utils.LogUtils;
import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.arialyy.aria.core.inf.AbsEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;

public class MyDownloadAct extends BaseActivity {
    RecyclerView mDownloadRV;
    DownloadListAdapter mAdapter;
    private DownloadManager downloadManager;

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
        downloadManager = DownloadService
                .getDownloadManager(this.getApplicationContext());
        mockData();
        Aria.download(this).register();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mockData();
        }
    };

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
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void mockData() {
        List<DownloadItemBean> beans = new ArrayList<>();
        List<AbsEntity> list = Aria.download(App.getContext()).getTotleTaskList();
        LogUtils.e("list="+list);
        if (list == null) return;
        for (AbsEntity entity : list) {
            DownloadItemBean bean = new DownloadItemBean();
            bean.avatarUrl = entity.getKey();
            bean.process = entity.getPercent();
            LogUtils.e("entity.getCompleteTime()=" + entity.getCompleteTime()+";bean.process ="+bean.process );
            beans.add(bean);
        }
        mAdapter.setData(beans);
    }

    private List<DownloadInfo> getDownloadListData() {
        return downloadManager.findAll();
    }

    public static void start(Context context, MyBusinessInfLocal myBusinessInfo) {
        download(myBusinessInfo);
        Intent intent = new Intent(context, MyDownloadAct.class);
//        intent.putExtra(DOWN, myBusinessInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void download(MyBusinessInfLocal myBusinessInfo) {
        String name = "123123";
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
        Aria.download(App.getContext())
                .load(url,true)
                .resetState()//读取下载地址
                .setDownloadPath(path)    //设置文件保存的完整路径
                .start();
    }

    public static final String DOWNLOAD_REFRESH = "download_refresh";

    private static void notifyRefresh() {
        Intent intent = new Intent(DOWNLOAD_REFRESH);
        notify(intent);
    }

    private static void notify(Intent intent) {
        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
    }
}
