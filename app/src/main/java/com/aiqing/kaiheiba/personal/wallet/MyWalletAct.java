package com.aiqing.kaiheiba.personal.wallet;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;

import io.reactivex.functions.Consumer;
import user.UserService;

public class MyWalletAct extends BaseActivity {
    TextView tvMoney, tvBao;
    RelativeLayout recordlayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("我的钱包");
        setContentView(R.layout.activity_my_wallet);
        hideShadow();
        tvMoney = findViewById(R.id.v_wallet_balance);
        tvBao = findViewById(R.id.v_wallet_lsb_num);
        findViewById(R.id.v_wallet_cash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("敬请期待");
            }
        });
        findViewById(R.id.v_wallet_charge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                toast("敬请期待");
            }
        });
        ApiManager.INSTANCE.getApi(WalletApi.class, "http://sdk.17kaiheiba.com").queryUserInfo(UserService.getUserToken())
                .compose(RxSchedulers.<WalletApi.WalletBean>compose())
                .subscribe(new Consumer<WalletApi.WalletBean>() {
                    @Override
                    public void accept(WalletApi.WalletBean walletBean) throws Exception {
                        int money = 0;
                        String wMoney = walletBean.getMoney();
                        if (!TextUtils.isEmpty(wMoney)) {
                            money = (int) Double.parseDouble(wMoney);
                        }
                        tvMoney.setText("价值" + money * 10 + " (元)");
                        tvBao.setText(money + "包");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }
}
