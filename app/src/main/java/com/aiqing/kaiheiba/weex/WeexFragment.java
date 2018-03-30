package com.aiqing.kaiheiba.weex;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseFragment;
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
    public static String gameurl = "http://172.16.244.1:8080/dist/index.weex.js";
//    public static String gameurl = "http://192.168.1.115:8080/dist/index.weex.js";
    public static String homeurl = "http://172.16.244.1:8080/dist/home.weex.js";
    public static String mypageurl = "http://192.168.1.115:8080/dist/mypage.weex.js";

    public WeexFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.frg_weex, null);
        rootView = view.findViewById(R.id.weex_frg_container);
        url = getArguments() == null ? null : getArguments().getString(WXSDKInstance.BUNDLE_URL);
        tag = getArguments() == null ? null : getArguments().getString("tag");
        mWxInstance = new WXSDKInstance(getActivity());
        mWxInstance.registerRenderListener(this);
        Map<String, Object> options = new HashMap<>();
        options.put(WXSDKInstance.BUNDLE_URL, url);
        mWxInstance.renderByUrl(getActivity().getPackageName(), url, options, null, WXRenderStrategy.APPEND_ASYNC);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeAllViews();
        }
        return rootView;
    }

    public static WeexFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(WXSDKInstance.BUNDLE_URL, url);
        WeexFragment fragment = new WeexFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        rootView.addView(view);
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {

    }
}
