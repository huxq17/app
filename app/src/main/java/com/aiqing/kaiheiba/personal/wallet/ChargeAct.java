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
import com.aiyou.sdk.Constants.Constants;
import com.aiyou.sdk.LGSDK;
import com.aiyou.sdk.bean.UserInfo;
import com.aiyou.sdk.listener.SDKListener;

public class ChargeAct extends BaseActivity implements SDKListener {
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
        LGSDK.LGSetListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LGSDK.LGSetListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LGSDK.onActivityResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LGSDK.onActivityPause(this);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ChargeAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onPayFinished(int i, String s) {
        switch (i) {
            case Constants.PAY_CANCEL:
                //用户取消付款
            case Constants.PAY_FAIL:
                //支付失败
                //这里支付没有成功，不需要发货
                break;
            default:
                //去后台查询支付结果
                MyWalletAct.start(this);
                break;
        }
        toast(s);
    }

    @Override
    public void onLoginSuccess(UserInfo userInfo) {

    }

    @Override
    public void onLoginFail(String s) {

    }

    @Override
    public void onLoginOut() {

    }

    @Override
    public void onSwitchAccount() {

    }

    @Override
    public void onSdkExit() {

    }

    @Override
    public boolean onRequestFloatBallPermission() {
        return false;
    }
}
