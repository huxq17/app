package com.aiqing.kaiheiba.weex;

import android.app.Activity;

import com.huxq17.xprefs.XPrefs;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.HashMap;
import java.util.Map;

public class WeexWindowSizeModule extends WXModule {
    public static class WindowSize {
        public int width;
        public int height;
        public int bottomBarHeight;

        public WindowSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public WindowSize() {
        }
    }

    public Activity getActivity() {
        return (Activity) mWXSDKInstance.getContext();
    }

    @JSMethod
    public void getSize(JSCallback callback) {
        Map<String, Object> map = new HashMap<>();
        WeexWindowSizeModule.WindowSize windowSize = XPrefs.get(WeexWindowSizeModule.WindowSize.class);
        map.put("width", windowSize.width);
        map.put("height", windowSize.height);
        Activity activity = getActivity();
        if (activity == null) return;
        int screenHeight = activity.findViewById(android.R.id.content).getHeight();
        map.put("screenHeight", screenHeight);

        callback.invoke(map);
    }

}