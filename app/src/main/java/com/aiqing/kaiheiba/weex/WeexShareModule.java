package com.aiqing.kaiheiba.weex;

import com.aiqing.kaiheiba.utils.ShareUtils;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

public class WeexShareModule extends WXModule {

    @JSMethod
    public void SharePost(com.alibaba.fastjson.JSONObject jsonObject, JSCallback callback) {
        String title = jsonObject.getString("title");
        String image = jsonObject.getString("image");
        String url = jsonObject.getString("URL");
        ShareUtils.share(WXEnvironment.getApplication(), title, title + url);
    }

}