package com.aiqing.kaiheiba.neteasyim.main.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.neteasyim.MainTab;
import com.netease.nim.uikit.common.fragment.TabFragment;


public abstract class MainTabFragment extends TabFragment {

    private boolean loaded = false;
    private MainTab tabData;

    protected abstract void onInit();

    protected boolean inited() {
        return loaded;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_tab_fragment_container, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void attachTabData(MainTab tabData) {
        this.tabData = tabData;
    }

    @Override
    public void onCurrent() {
        super.onCurrent();

        if (!loaded ) {
            loadRealLayout();
            loaded = true;
            onInit();
        }
    }

    @Override
    public void onDestroy() {
        loaded = false;
        super.onDestroy();
    }

    private boolean loadRealLayout() {
        ViewGroup root = (ViewGroup) getView();
        if (root != null) {
            root.removeAllViewsInLayout();
            View.inflate(root.getContext(), tabData.layoutId, root);
            onLayoutLoaded(root);
        }
        return root != null;
    }

    public void onLayoutLoaded(ViewGroup parent) {

    }
}
