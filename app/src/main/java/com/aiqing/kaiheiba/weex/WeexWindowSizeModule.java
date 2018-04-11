package com.aiqing.kaiheiba.weex;

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

        public WindowSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public WindowSize() {
        }
    }

    @JSMethod
    public void getSize(JSCallback callback) {
        Map<String, Object> map = new HashMap<>();
        WeexWindowSizeModule.WindowSize windowSize = XPrefs.get(WeexWindowSizeModule.WindowSize.class);
        map.put("width", windowSize.width);
        map.put("height", windowSize.height);
        callback.invoke(map);
    }

}