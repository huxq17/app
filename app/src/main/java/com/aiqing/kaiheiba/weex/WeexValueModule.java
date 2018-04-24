package com.aiqing.kaiheiba.weex;

import android.util.Log;

import com.huxq17.xprefs.LogUtils;
import com.huxq17.xprefs.SPUtils;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.HashMap;
import java.util.Map;

import user.UserService;

public class WeexValueModule extends WXModule {

    @JSMethod
    public void getIDAndTokenCallback(JSCallback callback) {
        Map<String, Object> map = new HashMap<>();
        String userId = UserService.getUserId();
        String usertoken = UserService.getUserToken();
//        if(TextUtils.isEmpty(userId)|| TextUtils.isEmpty(usertoken)){
//            LoginAct.start(WXEnvironment.getApplication());
//        }
        map.put("userID", userId);
        map.put("userToken", usertoken);
        callback.invoke(map);
        //callback.invoke(map);
    }

    @JSMethod
    public void getAccountIdCallback(JSCallback callback) {
//        if(TextUtils.isEmpty(userId)|| TextUtils.isEmpty(usertoken)){
//            LoginAct.start(WXEnvironment.getApplication());
//        }
        String accountId = SPUtils.getString(WXEnvironment.getApplication(), "waccountId");
        LogUtils.e("accountId="+accountId);
        callback.invoke(accountId);
        //callback.invoke(map);
    }

    @JSMethod
    public void log(String str) {
        Log.e("123", str);
    }
}