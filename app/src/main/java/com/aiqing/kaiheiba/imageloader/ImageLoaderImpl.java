package com.aiqing.kaiheiba.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.buyi.huxq17.serviceagency.annotation.ServiceAgent;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

@ServiceAgent
public class ImageLoaderImpl implements ImageLoader {
    private ImageLoaderImpl() {
//        throw new AssertionError();
    }

    @Override
    public void loadImage(String url, ImageView imageview) {
        if (url == null || url.isEmpty()) return;
        Context context = imageview.getContext();
        Picasso.with(context)
                .load(url)
                .config(Bitmap.Config.RGB_565)
                .into(imageview);
    }

    @Override
    public void loadImage(String url, ImageView imageview, int width, int height) {
        if (url == null || url.isEmpty()) return;
        Context context = imageview.getContext();
        if (imageview.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            Picasso.with(context)
                    .load(url)
                    .fit()
                    .config(Bitmap.Config.RGB_565)
                    .centerCrop()
                    .into(imageview);
        } else if (imageview.getScaleType() == ImageView.ScaleType.FIT_CENTER) {
            Picasso.with(context)
                    .load(url)
                    .fit()
                    .config(Bitmap.Config.RGB_565)
                    .centerInside()
                    .into(imageview);
        } else if (imageview.getScaleType() == ImageView.ScaleType.FIT_XY) {
            Picasso.with(context)
                    .load(url)
                    .fit()
                    .config(Bitmap.Config.RGB_565)
                    .into(imageview);
        }
    }

    @Override
    public void loadBigImage(String url, ImageView imageview) {
        if (url == null || url.isEmpty()) return;
        Context context = imageview.getContext();
        Picasso.with(context)
                .load(url)
                .fit()
                .centerInside()
                .config(Bitmap.Config.RGB_565)
//                .transform(getTransformation(imageview))
                .into(imageview);
    }

    private Transformation getTransformation(final ImageView view) {
        return new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int targetWidth = view.getWidth();

                //返回原图
                if (source.getWidth() == 0 || source.getWidth() < targetWidth) {
                    return source;
                }

                //如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                if (targetHeight == 0 || targetWidth == 0) {
                    return source;
                }

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };
    }
}
