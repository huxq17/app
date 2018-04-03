package com.aiqing.kaiheiba.okhttp;


import android.text.TextUtils;

import com.aiqing.kaiheiba.App;
import com.aiqing.kaiheiba.utils.Device;
import com.aiqing.kaiheiba.utils.DeviceHelper;
import com.aiqing.kaiheiba.utils.MD5;
import com.aiqing.kaiheiba.utils.Utils;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import user.UserService;

public class CommonInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        FormBody.Builder formBodyBuilder = new FormBody.Builder()//form表单
                .add("platform", "2")
                .add("os", DeviceHelper.getSysVersion())
                .add("model", DeviceHelper.getModel())
                .add("udid", Device.getDeviceId(App.getContext()))
                .add("sdk_version", "1.0.0")
                .add("app_id", "");
        //如果是post请求的话就把参数重新拼接一下，get请求的话就可以直接加入公共参数了
        if (originalRequest.method().equals("POST")) {
            RequestBody originalBody = originalRequest.body();
            if (originalBody instanceof FormBody) {
                FormBody body = (FormBody) originalBody;
                for (int i = 0; i < body.size(); i++) {
                    formBodyBuilder.add(body.name(i), body.value(i));
                }
            }
        }
        RequestBody formBody = formBodyBuilder.build();

        String postBodyString = bodyToString(originalRequest.body());
        postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        String[] strings = postBodyString.split("&");
        for (String s : strings) {
            String[] value = s.split("=");
            String key = value[0];
            String value1;
            if (value.length == 1) {
                value1 = "";
            } else {
                value1 = value[1];
            }
            params.put(key, value1);
        }
        String s = sign(params);
        postBodyString = postBodyString.concat("&sign=").concat(s);
        Request.Builder requestBuilder = originalRequest.newBuilder();
        String userId = UserService.getUserId();
        String userToken = UserService.getUserToken();
//        LogUtils.e("userid="+userId+";usertoken="+userToken);
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userToken)) {
//            builder.header("X-Auth-UserId", "1");
//            builder.header("X-Auth-Token", "");
            requestBuilder.header("X-Auth-UserId", userId);
            requestBuilder.header("X-Auth-Token", userToken);
        }
        Request request = requestBuilder.method(originalRequest.method(), originalRequest.body())
                //添加到请求里
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        postBodyString))
                .build();
        return chain.proceed(request);
    }

    private String sign(LinkedHashMap<String, Object> params) {
        String str = Utils.sortHashMap(params);
        return MD5.MD5WithAiyou(str);
    }

    private String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

}
