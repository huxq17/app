package com.aiqing.kaiheiba.weex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aiqing.kaiheiba.App;
import com.aiqing.kaiheiba.BuildConfig;
import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseFragment;
import com.huxq17.xprefs.SPUtils;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;

import java.util.HashMap;
import java.util.Map;

public class WeexFragment extends BaseFragment implements IWXRenderListener {
    private String url;
    private FrameLayout rootView;
    private WXSDKInstance mWxInstance;
    private String tag;
    public static final String gameurl = "index.weex.js";
    public static final String homeurl = "home.weex.js";
    public static final String mypageurl = "myPage.weex.js";
    public static final String onlineUrl = "http://weex.17kaiheiba.com/bundle/";
    public static final String userMainUrl = "userHome.weex.js";
    private boolean isSucceeded = false;
    private boolean isFirst = true;

    public WeexFragment() {
    }

    public static String getRoot() {
        Context context = App.getContext();
        switch (BuildConfig.BUILD_TYPE) {
            case "debug":
                String baseUrl = SPUtils.getString(context, "baseUrl");
                if (TextUtils.isEmpty(baseUrl)) {
//                    baseUrl = "http://172.16.244.1:8080/dist/";
//                    baseUrl = "http://192.168.1.115:8080/dist/";
                }
                if (TextUtils.isEmpty(baseUrl)) {
                    baseUrl = onlineUrl;
                }
                return baseUrl;
        }
        return onlineUrl;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWxInstance != null) {
            mWxInstance.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mWxInstance != null) {
            mWxInstance.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSucceeded = false;
        View view = View.inflate(getActivity(), R.layout.frg_weex, null);
        rootView = view.findViewById(R.id.weex_frg_container);
        url = getArguments() == null ? null : getArguments().getString(WXSDKInstance.BUNDLE_URL);
        tag = getArguments() == null ? null : getArguments().getString("tag");
        mWxInstance = new WXSDKInstance(getActivity());
        mWxInstance.registerRenderListener(this);
        isFirst = true;
        loadJs();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return rootView;
    }

    private void loadJs() {
        if (isSucceeded) return;
        Map<String, Object> options = new HashMap<>();
        options.put(WXSDKInstance.BUNDLE_URL, url);
        mWxInstance.renderByUrl(getActivity().getPackageName(), url, options, null, WXRenderStrategy.APPEND_ASYNC);
    }

    public void send() {
        if (mWxInstance != null) {
            Map<String, Object> params = new HashMap<>();
            mWxInstance.fireGlobalEventCallback("updateUserinfo", params);
        }
    }

    public static WeexFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(WXSDKInstance.BUNDLE_URL, getRoot() + url);
        WeexFragment fragment = new WeexFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        rootView.removeAllViews();
        rootView.addView(view);
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
        isSucceeded = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirst) {
            loadJs();
        }
        isFirst = false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadJs();
        }
    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {
    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
        if (rootView == null) return;
        rootView.removeAllViews();
        View.inflate(getActivity(), R.layout.layout_error, rootView)
                .findViewById(R.id.bt_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadJs();
            }
        });
    }
}
