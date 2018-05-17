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
import com.aiqing.kaiheiba.utils.CacheDataManager;
import com.aiqing.kaiheiba.widget.XCircleIndicator;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.view.BigImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageBrowserActivity extends BaseActivity {
    private static final String IMAGS_KEY = "images_key";
    private static final String IMAG_INDEX_KEY = "img_index_key";

    private ViewPager imageViewPager;
    private int lastPostion;
    private XCircleIndicator indicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_images);
        BigImageViewer.initialize(com.github.piasy.biv.loader.glide.GlideImageLoader.with(getApplicationContext()));
        int position;
        List<String> imageList;
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(IMAG_INDEX_KEY, 0);
            imageList = savedInstanceState.getStringArrayList(IMAGS_KEY);
        } else {
            position = getIntent().getIntExtra(IMAG_INDEX_KEY, 0);
            imageList = getIntent().getStringArrayListExtra(IMAGS_KEY);
        }
        imageViewPager = findViewById(R.id.vp_image_browser);
        indicator = findViewById(R.id.xCircleIndicator);
        imageViewPager.setOffscreenPageLimit(2);

        final PagerAdapter adapter = new ImageBrowserPagerAdapter(imageList);
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
                        if (childAt != null && childAt instanceof BigImageView) {
                            BigImageView bigImageView = (BigImageView) childAt;
                            bigImageView.getSSIV().resetScaleAndCenter();
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        indicator.initData(adapter.getCount(), 0);
        indicator.setCurrentPage(lastPostion);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageViewPager.setAdapter(null);
        File glideCachedFile = new File(getExternalCacheDir(), "glide/");
        CacheDataManager.limitFileCount(glideCachedFile, 12);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(IMAGS_KEY, getIntent().getStringArrayListExtra(IMAGS_KEY));
        outState.putInt(IMAG_INDEX_KEY, lastPostion);
    }

    public static void start(Context context, int index, ArrayList<String> imgList) {
        Intent intent = new Intent(context, ImageBrowserActivity.class);
        intent.putStringArrayListExtra(IMAGS_KEY, imgList);
        intent.putExtra(IMAG_INDEX_KEY, index);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
