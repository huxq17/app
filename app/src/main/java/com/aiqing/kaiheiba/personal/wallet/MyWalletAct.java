package com.aiqing.kaiheiba.personal.wallet;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;

import java.math.BigDecimal;

import io.reactivex.functions.Consumer;
import user.UserService;

import static com.aiqing.kaiheiba.api.ApiManager.TEST_BASE_URL;

public class MyWalletAct extends BaseActivity {
    TextView tvMoney, tvBao;
    RelativeLayout recordlayout;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

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
                ChargeAct.start(MyWalletAct.this);
            }
        });
        findViewById(R.id.v_wallet_trade_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TradeRecordAct.start(MyWalletAct.this);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String baseUrl = ApiManager.ISTest ? TEST_BASE_URL : "http://sdk.17kaiheiba.com";
        ApiManager.INSTANCE.getApi(WalletApi.class, baseUrl).queryUserInfo(UserService.getUserToken())
                .compose(RxSchedulers.<WalletApi.WalletBean>compose())
                .subscribe(new Consumer<WalletApi.WalletBean>() {
                    @Override
                    public void accept(WalletApi.WalletBean walletBean) throws Exception {
                        String wMoney =walletBean.getMoney();
                        tvMoney.setText(new StringBuilder("价值").append(wMoney).append(" (元)"));
                        tvBao.setText(getLingShiBao(wMoney) + "包");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public String getLingShiBao(String amount) {
        String currency = amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        int scale = 0;
        if (index == -1) {
            amLong = Long.valueOf(currency + "00");
        } else if (length - index >= 3) {
            amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
            scale=1;
        } else if (length - index == 2) {
            amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + 0);
        } else {
            amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
        }
        int fen = amLong.intValue();
        String result = BigDecimal.valueOf(fen).
                divide(new BigDecimal(10), scale, BigDecimal.ROUND_HALF_DOWN).toString();
        return result;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MyWalletAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
