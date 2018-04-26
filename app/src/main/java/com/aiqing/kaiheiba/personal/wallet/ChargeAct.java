package com.aiqing.kaiheiba.personal.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;

public class ChargeAct extends BaseActivity {
    private RecyclerView mRvChargeList;
    private ChargeListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("充值");
        setContentView(R.layout.activity_charge_list);
        mRvChargeList = findViewById(R.id.charge_list);
        mAdapter = new ChargeListAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvChargeList.setLayoutManager(linearLayoutManager);
        mRvChargeList.setAdapter(mAdapter);
        ApiManager.INSTANCE.getApi(WalletApi.class).queryChargeList()
                .compose(RxSchedulers.<ChargeListBean>compose())
                .subscribe(new BaseObserver<ChargeListBean.DataBean>() {
                    @Override
                    protected void onSuccess(ChargeListBean.DataBean dataBean) {
                        mAdapter.setData(dataBean.getAndroid());
                    }
                });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ChargeAct.class);
        context.startActivity(intent);
    }
}
