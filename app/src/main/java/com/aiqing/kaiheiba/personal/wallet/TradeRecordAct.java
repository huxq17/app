package com.aiqing.kaiheiba.personal.wallet;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

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
    private List<Fragment> mTabContents = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private List<String> mDatas = Arrays.asList("全部", "收入", "支出");
    private TabLayout mTabLayout;
    private NumberPicker mDatePicker;
    private ViewGroup flDatePick;
    private String[] mMonth = {"01", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private ViewGroup selectDateLayout;

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
        initDatas();
        //设置Tab上的标题
//        mTabLayout.setData(mDatas);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mViewPager.setAdapter(mAdapter);
        ApiManager.INSTANCE.getApi(WalletApi.class).queryRecordInfo("2018-04")
                .compose(RxSchedulers.<WalletApi.RecordBean>compose())
                .subscribe(new BaseObserver<WalletApi.RecordBean.DataBean>() {
                    @Override
                    protected void onSuccess(WalletApi.RecordBean.DataBean dataBean) {
                        List<WalletApi.RecordBean.DataBean.ResultBean> list = dataBean.getResult();
                        toast("list.size=" + list.size());
                    }
                });
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

    private void initView() {
        mViewPager = findViewById(R.id.record_viewpager);
        mTabLayout = findViewById(R.id.record_tab_layout);
        flDatePick = findViewById(R.id.fl_date_picker);
        mDatePicker = findViewById(R.id.date_picker);
        initDatePicker();
        findViewById(R.id.layout_date_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        showDatePicker(false);
    }

    private void initDatePicker() {
        mDatePicker.setFormatter(this); //格式化数字，需重写format方法，详情见下面的代码
        mDatePicker.setOnValueChangedListener(this); //值变化监听事件
        mDatePicker.setMinValue(1);//最小值
        mDatePicker.setMaxValue(12);//最大值
        mDatePicker.setValue(4);//设置初始选定值
    }

    public void showDatePicker(boolean show) {
        flDatePick.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public String format(int value) {
        if (value < 10) {
            return "0" + value;
        } else {
            return "" + value;
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }
}
