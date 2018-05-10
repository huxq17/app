package com.aiqing.kaiheiba.imagebrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ImageBrowserViewPager extends ViewPager {
    public ImageBrowserViewPager(@NonNull Context context) {
        super(context);
    }

    public ImageBrowserViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }
}
