package com.aiqing.kaiheiba.api;


import com.aiqing.kaiheiba.okhttp.CommonInterceptor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory2;

public enum ApiManager {
    INSTANCE;
    private static final HashMap<Class, Object> mCachedApi = new LinkedHashMap<>();
    public static final String BASE_URL = "http://116.62.139.161/";
    OkHttpClient okHttpClient;

    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(new CommonInterceptor())
                    .build();
        }
        return okHttpClient;
    }

    @SuppressWarnings("unchecked")
    public <T> T getApi(Class<T> tClass, String baseUrl) {
        Object cacheApi = mCachedApi.get(tClass);
        T api = null;
        if (cacheApi != null && cacheApi.getClass() == tClass) {
            api = (T) cacheApi;
        }
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory2.create())
                    .build();
            api = retrofit.create(tClass);
            mCachedApi.put(tClass, api);
        }
        return api;
    }


    public <T> T getApi(Class<T> apiClass) {
        return getApi(apiClass, BASE_URL);
    }

//    public <T> T getDownloadApi(Class<T> apiClass, String url) {
//        return getApi(apiClass, url);
//    }

    public void clear() {
        mCachedApi.clear();
    }
}
