package com.taobao.weex.cache;

public class CacheBean {
    public String url;
    public String md5;
    public String content;
    public String ims;

    public CacheBean(String url, String md5, String content, String ims) {
        this.url = url;
        this.content = content;
        this.md5 = md5;
        this.ims = ims;
    }

    public static String getKeyByUrl(String url) {
        return url.split("\\?")[0];
    }
}
