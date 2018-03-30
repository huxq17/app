package com.aiqing.kaiheiba.personal.wallet;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.widget.tablayout.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TradeRecordAct extends BaseActivity {
    private List<Fragment> mTabContents = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private List<String> mDatas = Arrays.asList("全部", "收入", "提现");
    private TabLayout mTabLayout;

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
    }
}
