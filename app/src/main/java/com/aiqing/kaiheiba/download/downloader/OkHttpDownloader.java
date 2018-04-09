package com.aiqing.kaiheiba.download.downloader;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class OkHttpDownloader implements NetworkDownloader {
    private static OkHttpClient defaultOkHttpClient() {
        OkHttpClient  client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        return client;
    }

    private final OkHttpClient client;

    public OkHttpDownloader() {
        client = defaultOkHttpClient();
    }

    @Override
    public Response load(String url, String range) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (range != null && !range.isEmpty()) {
            builder.header("RANGE", range);
        }
        okhttp3.Response response = client.newCall(builder.build()).execute();
        int responseCode = response.code();
        if (responseCode >= 300) {
            response.body().close();
            return null;
        }
        ResponseBody responseBody = response.body();
        return new Response(responseBody.byteStream(), responseBody.contentLength());
    }
}
