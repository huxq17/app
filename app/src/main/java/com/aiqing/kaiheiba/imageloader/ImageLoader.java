package com.aiqing.kaiheiba.imageloader;

import android.widget.ImageView;

public interface ImageLoader {
    void loadImage(String url, ImageView imageview);
    void loadImage(String url, ImageView imageview,int width,int height);
    void test(String msg);
}
