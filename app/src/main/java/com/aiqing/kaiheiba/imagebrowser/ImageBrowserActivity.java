package com.aiqing.kaiheiba.imagebrowser;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class ImageBrowserActivity extends BaseActivity {
    private static final String IMAGS_KEY = "images_key";
    private static final String IMAG_INDEX_KEY = "img_index_key";

    private ViewPager imageViewPager;
    private int lastPostion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_images);
        int position = getIntent().getIntExtra(IMAG_INDEX_KEY, 0);

        List<String> imageList = getIntent().getStringArrayListExtra(IMAGS_KEY);

        imageViewPager = findViewById(R.id.vp_image_browser);
        imageViewPager.setOffscreenPageLimit(2);

        final PagerAdapter adapter = new ImageBroswerPagerAdapter(imageList);
        imageViewPager.setAdapter(adapter);
        lastPostion = position;
        imageViewPager.setCurrentItem(lastPostion);
        imageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (lastPostion != position && positionOffsetPixels == 0) {
                    lastPostion = position;
                    int childCount = imageViewPager.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = imageViewPager.getChildAt(i);
                        if (childAt != null && childAt instanceof PhotoView) {
                            PhotoView photoView = (PhotoView) childAt;
                            photoView.getAttacher().setScale(1);
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public static void start(Context context, int index, ArrayList<String> imgs) {
        Intent intent = new Intent(context, ImageBrowserActivity.class);
        intent.putStringArrayListExtra(IMAGS_KEY, imgs);
        intent.putExtra(IMAG_INDEX_KEY, index);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
