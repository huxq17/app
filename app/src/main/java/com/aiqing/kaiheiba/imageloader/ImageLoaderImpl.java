package com.aiqing.kaiheiba.imageloader;

import android.widget.ImageView;

import com.buyi.huxq17.serviceagency.annotation.ServiceAgent;
import com.squareup.picasso.Picasso;

@ServiceAgent
public class ImageLoaderImpl implements ImageLoader {
    private ImageLoaderImpl() {
//        throw new AssertionError();
    }

    @Override
    public void loadImage(String url, ImageView imageview) {
        if (url == null || url.isEmpty()) return;
        Picasso.with(imageview.getContext())
                .load(url)
                .into(imageview);
    }

    @Override
    public void test(String msg) {
        System.out.println("test msg=" + msg);
    }
}
