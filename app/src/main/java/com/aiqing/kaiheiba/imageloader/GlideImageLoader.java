package com.aiqing.kaiheiba.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.andbase.tractor.utils.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

//@ServiceAgent
public class GlideImageLoader implements ImageLoader {
    private GlideImageLoader() {
//        throw new AssertionError();
    }

    @Override
    public void loadImage(String url, ImageView imageview) {
        if (url == null || url.isEmpty()) return;
        Context context = imageview.getContext();
        Glide.with(context)
                .load(url)
                .into(imageview);
    }

    @Override
    public void loadImage(String url, ImageView imageview, int width, int height) {
        if (url == null || url.isEmpty()) return;
        Context context = imageview.getContext();
        LogUtils.e("loadImage CENTER_CROP width="+width+";height="+height);
        if (imageview.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .override(width, height)
                    )
                    .into(imageview);
        } else if (imageview.getScaleType() == ImageView.ScaleType.FIT_CENTER) {
            LogUtils.e("loadImage FIT_CENTER");
            Glide.with(context)
                    .load(url)
                    .into(imageview);
        } else if (imageview.getScaleType() == ImageView.ScaleType.FIT_XY) {
            LogUtils.e("loadImage FIT_XY width="+width+";height="+height);
            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .override(width, height)
                    )
                    .into(imageview);
        }
    }

    @Override
    public void loadBigImage(String url, ImageView imageview) {
        if (url == null || url.isEmpty()) return;
        Context context = imageview.getContext();
        Glide.with(context)
                .load(url)
                .into(imageview);
    }
}
