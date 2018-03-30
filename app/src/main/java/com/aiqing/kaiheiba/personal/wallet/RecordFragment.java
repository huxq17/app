package com.aiqing.kaiheiba.personal.wallet;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends android.support.v4.app.Fragment {
    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "DefaultValue";
    private RecyclerView recyclerView;
    private TradeRecordAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTitle = arguments.getString(BUNDLE_TITLE);
        }
        recyclerView = new RecyclerView(getActivity());
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutmanager);
        mAdapter = new TradeRecordAdapter();
        recyclerView.setBackgroundColor(Color.WHITE);
        recyclerView.setAdapter(mAdapter);
        mockData();
        return recyclerView;
    }

    private void mockData() {
        List<TradeBean> list = new ArrayList<>();
        list.add(new TradeBean());
        list.add(new TradeBean());
        list.add(new TradeBean());
        mAdapter.setData(list);
    }

    public static RecordFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
