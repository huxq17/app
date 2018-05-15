package com.aiqing.kaiheiba.imagebrowser;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import com.aiqing.kaiheiba.utils.DensityUtil;
import com.aiqing.kaiheiba.widget.BottomAnimDialog;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.view.BigImageView;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.aiqing.imagepicker.utils.MediaUtils.createNewFile;
import static com.aiqing.imagepicker.utils.MediaUtils.fileScan;

public class ImageBrowserPagerAdapter extends PagerAdapter implements View.OnLongClickListener, View.OnClickListener {
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
    public Object instantiateItem(ViewGroup container, final int position) {

        final String imgUrl = imgs.get(position);
        final BigImageView imageView = new BigImageView(container.getContext());
        imageView.showImage(Uri.parse(imgUrl));
//        ServiceAgency.getService(ImageLoader.class).loadBigImage(imgUrl, photoView);
        container.addView(imageView);
        imageView.setProgressIndicator(new ProgressPieIndicator());
        imageView.setOnLongClickListener(null);
        imageView.setOnClickListener(null);
        imageView.setImageLoaderCallback(new ImageLoader.Callback() {
            @Override
            public void onCacheHit(File image) {

            }

            @Override
            public void onCacheMiss(File image) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess(File image) {
                imageView.setTag(image.getAbsolutePath());
                imageView.setOnLongClickListener(ImageBrowserPagerAdapter.this);
                imageView.setOnClickListener(ImageBrowserPagerAdapter.this);
            }

            @Override
            public void onFail(Exception error) {

            }
        });
        return imageView;
    }

    @Override
    public boolean onLongClick(final View v) {
        ViewParent parent = v.getParent();
        if (!(parent instanceof BigImageView)) {
            return false;
        }
        BigImageView bigImageView = (BigImageView) parent;
        final String imagePath = (String) bigImageView.getTag();
        if (TextUtils.isEmpty(imagePath)) return false;
        final Context context = v.getContext();
        BottomAnimDialog bottomAnimDialog = new BottomAnimDialog((Activity) context);
        int itemHeight = DensityUtil.dip2px(context, 50);
        bottomAnimDialog.addItem(new BottomAnimDialog.Item("保存图片", new BottomAnimDialog.OnClickListener() {
            @Override
            public void onClick() {
                saveImageIntoGallery(context, imagePath);
            }
        }).height(itemHeight))
                .addItem(new BottomAnimDialog.Item("取消", new BottomAnimDialog.OnClickListener() {
                    @Override
                    public void onClick() {

                    }
                }).height(itemHeight))
                .build();
        bottomAnimDialog.show();
        return true;
    }

    public void saveImageIntoGallery(final Context context, final String path) {
        if (!EasyPermissions.hasPermissions(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }
        File imageFile = new File(path);
        if (!imageFile.exists()) {
            return;
        }
        File picFile = createNewFile(context, new Bundle(), false);
        imageFile.renameTo(picFile);
        imageFile.delete();
        fileScan(context, picFile.getAbsolutePath());
        Toast.makeText(context, "保存成功,存在" + picFile.getParentFile().getAbsolutePath() + "路径下", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        Activity activity = (Activity) v.getContext();
        activity.finish();
    }
}
