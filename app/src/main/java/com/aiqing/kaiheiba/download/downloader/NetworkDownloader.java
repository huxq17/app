package com.aiqing.kaiheiba.download.downloader;


import java.io.IOException;
import java.io.InputStream;

public interface NetworkDownloader {
    Response load(String url, String range) throws IOException;

    class Response {
        public InputStream inputStream;
        public long contentLength;

        public Response(InputStream inputStream, long contentLength) {
            this.inputStream = inputStream;
            this.contentLength = contentLength;
        }
    }
}
