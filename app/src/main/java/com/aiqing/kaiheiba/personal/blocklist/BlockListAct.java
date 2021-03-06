package com.aiqing.kaiheiba.personal.blocklist;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.RelationshipApi;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.decoration.FansDivider;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.aiqing.kaiheiba.utils.DensityUtil;

import java.util.List;

public class BlockListAct extends BaseActivity {
    RecyclerView mFansList;
    BlockListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("屏蔽列表");
        setContentView(R.layout.activity_my_fans);
        mFansList = findViewById(R.id.myfans_list);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        mFansList.setLayoutManager(layoutmanager);
        mAdapter = new BlockListAdapter();
        mFansList.addItemDecoration(new FansDivider(this, LinearLayoutManager.HORIZONTAL,
                DensityUtil.dip2px(this, 1), getResources().getColor(R.color.bgSpace)));
        mFansList.setAdapter(mAdapter);
        obtainFollowers();
    }

    public void obtainFollowers() {
        ApiManager.INSTANCE.getApi(RelationshipApi.class).getBlockedList()
                .compose(RxSchedulers.<RelationshipApi.BlockBean>compose())
                .subscribe(new BaseObserver<List<RelationshipApi.BlockBean.DataBean>>(this) {
                    @Override
                    protected void onSuccess(List<RelationshipApi.BlockBean.DataBean> datas) {
                        applyData(datas);
                    }
                });
    }

    public void applyData(List<RelationshipApi.BlockBean.DataBean> datas) {
        mAdapter.setData(datas);
    }
}
