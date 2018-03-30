package com.aiqing.kaiheiba.okhttp;


import com.aiqing.kaiheiba.App;
import com.aiqing.kaiheiba.utils.Device;
import com.aiqing.kaiheiba.utils.DeviceHelper;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class CommonInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        FormBody.Builder formBodyBuilder = new FormBody.Builder()//form表单
                .add("platform", "2")
                .add("os", DeviceHelper.getSysVersion())
                .add("model", DeviceHelper.getModel())
                .add("udid", Device.getDeviceId(App.getContext()))
                .add("sdk_version", "1.0.0");
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

        Request request = originalRequest.newBuilder()
                .method(originalRequest.method(), originalRequest.body())
                //添加到请求里
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        postBodyString))
                .build();
        return chain.proceed(request);
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
