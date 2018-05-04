package com.taobao.weex.cache;

public class CacheBean {
    public String url;
    public String md5;
    public String content;

    public CacheBean(String url, String md5, String content) {
        this.url = url;
        this.content = content;
        this.md5 = md5;
    }
}
