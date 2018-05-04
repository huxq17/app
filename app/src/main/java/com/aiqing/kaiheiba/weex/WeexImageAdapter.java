package com.aiqing.kaiheiba.weex;

import android.text.TextUtils;
import android.widget.ImageView;

import com.aiqing.kaiheiba.imageloader.ImageLoader;
import com.buyi.huxq17.serviceagency.ServiceAgency;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;

public class WeexImageAdapter implements IWXImgLoaderAdapter {
    @Override
    public void setImage(final String url, final ImageView view, WXImageQuality quality, WXImageStrategy strategy) {
        WXSDKManager.getInstance().postOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view == null || view.getLayoutParams() == null) {
                    return;
                }
                if (TextUtils.isEmpty(url)) {
                    view.setImageBitmap(null);
                    return;
                }
                String temp = url;
                if (url.startsWith("//")) {
                    temp = "http:" + url;
                }
                int width = view.getLayoutParams().width;
                int height = view.getLayoutParams().height;
                if (width <= 0 || height <= 0) {
                    return;
                }
                ImageLoader imageLoader = ServiceAgency.getService(ImageLoader.class);
                imageLoader.loadImage(temp, view, width, height);
            }
        }, 0);
    }
}