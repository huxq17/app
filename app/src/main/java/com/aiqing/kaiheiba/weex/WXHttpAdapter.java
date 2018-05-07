package com.aiqing.kaiheiba.weex;

import android.text.TextUtils;

import com.andbase.tractor.utils.LogUtils;
import com.taobao.weex.adapter.IWXHttpAdapter;
import com.taobao.weex.common.WXRequest;
import com.taobao.weex.common.WXResponse;

import java.io.IOException;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WXHttpAdapter implements IWXHttpAdapter {

    private OkHttpClient mOkHttpClient;

    public WXHttpAdapter(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }

    @Override
    public void sendRequest(final WXRequest request, final OnHttpListener listener) {

        if (listener != null) {
            listener.onHttpStart();
        }
        if (request == null) {
            if (listener != null) {
                WXResponse wxResponse = new WXResponse();
                wxResponse.errorMsg = "WXRequest为空";
                listener.onHttpFinish(wxResponse);
            }
            return;
        }
        Request okHttpRequest = null;
        String requestMethod = request.method;
        if (TextUtils.isEmpty(requestMethod)) {
            requestMethod = "GET";
        }
        if ("POST".equalsIgnoreCase(requestMethod)) {
            Request.Builder builder = new Request.Builder()
                    .headers(getHeaders(request))
                    .url(request.url);
            if (request.body != null) {
                builder.post(RequestBody.create(MediaType.parse(request.body), request.body));
            } else {
                builder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded ;charset=utf-8"), ""));
            }
            okHttpRequest = builder.build();
        } else if ("GET".equalsIgnoreCase(requestMethod)) {
            okHttpRequest = new okhttp3.Request.Builder().url(request.url).build();
        } else if ("HEAD".equalsIgnoreCase(requestMethod)) {
            okHttpRequest = new okhttp3.Request.Builder().url(request.url).head().build();
        }
        if (okHttpRequest == null)
            throw new RuntimeException("does not supprot " + request.method + " method.");
        mOkHttpClient.newCall(okHttpRequest)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (listener != null) {
                            WXResponse wxResponse = new WXResponse();
                            wxResponse.errorCode = String.valueOf(-100);
                            wxResponse.statusCode = String.valueOf(-100);
                            wxResponse.errorMsg = e.getMessage();
                            try {
                                listener.onHttpFinish(wxResponse);
                            } catch (Exception e1) {
                                LogUtils.d(e1.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (listener != null) {
                            WXResponse wxResponse = new WXResponse();
                            int statusCode = response.code();
                            wxResponse.statusCode = String.valueOf(statusCode);
                            listener.onHeadersReceived(statusCode,response.headers().toMultimap());
                            ResponseBody body = response.body();
                            if (body == null) {
                                wxResponse.errorMsg = "body为空";
                            } else {
                                wxResponse.originalData = body.bytes();
                                body.close();
                            }
                            try {
                                listener.onHttpFinish(wxResponse);
                            } catch (Exception e) {
                                LogUtils.d(e.getMessage());
                            }
                        }
                    }
                });
    }

    private Headers getHeaders(WXRequest request) {
        okhttp3.Headers.Builder builder = new okhttp3.Headers.Builder();
        if (request.paramMap != null) {
            Set<String> keySet = request.paramMap.keySet();
            for (String key : keySet) {
                builder.add(key, request.paramMap.get(key));
            }
        }
        return builder.build();
    }
}