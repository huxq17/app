package com.aiqing.kaiheiba.download.downloader;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpDownloader implements NetworkDownloader {
    private static OkHttpClient defaultOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(Constants.DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(Constants.DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(Constants.DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
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
        com.squareup.okhttp.Response response = client.newCall(builder.build()).execute();
        int responseCode = response.code();
        if (responseCode >= 300) {
            response.body().close();
            return null;
        }
        ResponseBody responseBody = response.body();
        return new Response(responseBody.byteStream(), responseBody.contentLength());
    }
}
