package com.aiqing.kaiheiba.personal.wallet;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.aiqing.kaiheiba.widget.tablayout.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TradeRecordAct extends BaseActivity implements NumberPicker.OnValueChangeListener, NumberPicker.Formatter {
    private List<RecordFragment> mTabContents = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private List<String> mDatas = Arrays.asList("全部", "收入", "支出");
    private TabLayout mTabLayout;
    private NumberPicker mDatePicker;
    private View flDatePick;
    private TextView tvDate;

    public static void start(Context context) {
        Intent intent = new Intent(context, TradeRecordAct.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_record);
        setTitle("交易记录");
        initView();
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    private void initDatas() {
        for (String data : mDatas) {
            RecordFragment fragment = RecordFragment.newInstance(data);
            mTabContents.add(fragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mDatas.get(position);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mViewPager = findViewById(R.id.record_viewpager);
        mTabLayout = findViewById(R.id.record_tab_layout);
        tvDate = findViewById(R.id.tv_date);
        findViewById(R.id.layout_date_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        flDatePick = LayoutInflater.from(this).inflate(R.layout.layout_datepicker, null);
        mDatePicker = flDatePick.findViewById(R.id.date_picker);
        initDatas();
        mViewPager.setAdapter(mAdapter);
        initDatePicker();
    }

    private void initDatePicker() {
        int initalMonth = 4;
        mDatePicker.setFormatter(this);
        mDatePicker.setOnValueChangedListener(this);
        mDatePicker.setMinValue(1);
        mDatePicker.setMaxValue(12);
        mDatePicker.setValue(initalMonth);
        setDate(initalMonth);
        obtainData();
    }

    public void showDatePicker() {
        ViewGroup parent = (ViewGroup) flDatePick.getParent();
        if (parent != null) parent.removeView(flDatePick);
        new AlertDialog.Builder(this)
                .setTitle("选择月份")
                .setView(flDatePick)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        obtainData();
                    }
                })
                .show();
    }

    private void obtainData() {
        ApiManager.INSTANCE.getApi(WalletApi.class).queryRecordInfo(getText(tvDate))
                .compose(RxSchedulers.<WalletApi.RecordBean>compose())
                .subscribe(new BaseObserver<WalletApi.RecordBean.DataBean>() {
                    @Override
                    protected void onSuccess(WalletApi.RecordBean.DataBean dataBean) {
                        for (RecordFragment fragment : mTabContents) {
                            fragment.refreshData(dataBean.getResult());
                        }
                    }
                });
    }

    @Override
    public String format(int value) {
        return getDate(value);
    }

    public String getDate(int value) {
        if (value < 10) {
            return "0" + value;
        } else {
            return "" + value;
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        setDate(newVal);
    }

    private void setDate(int date) {
        tvDate.setText("2018-" + getDate(date));
    }
}
