package com.aiqing.kaiheiba.imagebrowser;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.aiqing.kaiheiba.imageloader.ImageLoader;
import com.buyi.huxq17.serviceagency.ServiceAgency;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class ImageBrowserPagerAdapter extends PagerAdapter {
    List<String> imgs;

    public ImageBrowserPagerAdapter(List<String> imgs) {
        this.imgs = imgs;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        String imgUrl = imgs.get(position);
        PhotoView photoView = new PhotoView(container.getContext());
        ServiceAgency.getService(ImageLoader.class).loadBigImage(imgUrl, photoView);
        container.addView(photoView);
        return photoView;
    }
}
