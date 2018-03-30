package com.aiqing.kaiheiba.api;


import com.aiqing.kaiheiba.okhttp.CommonInterceptor;
import com.aiqing.kaiheiba.okhttp.CustomInterceptor;

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

    @SuppressWarnings("unchecked")
    public <T> T getApi(Class<T> apiClass) {
        Object cacheApi = mCachedApi.get(apiClass);
        T api = null;
        if (cacheApi != null && cacheApi.getClass() == apiClass) {
            api = (T) cacheApi;
        }
        if (api == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new CustomInterceptor())
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(new CommonInterceptor())
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory2.create())
                    .build();
            api = retrofit.create(apiClass);
            mCachedApi.put(apiClass, api);
        }
        return api;
    }

    public void clear() {
        mCachedApi.clear();
    }
}
