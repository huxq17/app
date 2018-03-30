package com.aiqing.kaiheiba.okhttp;

import android.text.TextUtils;

import com.aiqing.kaiheiba.utils.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import user.UserService;

public class CustomInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request()
                .newBuilder();
        String userId = UserService.getUserId();
        String userToken = UserService.getUserToken();
        LogUtils.e("userid="+userId+";usertoken="+userToken);
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userToken)) {
//            builder.header("X-Auth-UserId", "1");
//            builder.header("X-Auth-Token", "");
            builder.header("X-Auth-UserId", userId);
            builder.header("X-Auth-Token", userToken);
        }
        return chain.proceed(builder.build());
    }
}
