package com.aiqing.kaiheiba.imageloader;

import android.widget.ImageView;

import com.buyi.huxq17.serviceagency.annotation.ServiceAgent;
import com.squareup.picasso.Picasso;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

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
    public void loadImage(String url, ImageView imageview, int width, int height) {
        if (url == null || url.isEmpty()) return;
        if (imageview.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            Picasso.with(imageview.getContext())
                    .load(url)
                    .fit()
                    .centerCrop()
                    .into(imageview);
        } else if (imageview.getScaleType() == ImageView.ScaleType.FIT_CENTER) {
            Picasso.with(imageview.getContext())
                    .load(url)
                    .memoryPolicy(NO_CACHE, NO_STORE)
                    .into(imageview);
        } else if (imageview.getScaleType() == ImageView.ScaleType.FIT_XY) {
            Picasso.with(imageview.getContext())
                    .load(url)
                    .into(imageview);
        }

    }

    @Override
    public void test(String msg) {
        System.out.println("test msg=" + msg);
    }
}
