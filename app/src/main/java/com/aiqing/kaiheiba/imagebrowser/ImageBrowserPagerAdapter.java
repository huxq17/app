package com.aiqing.kaiheiba.imagebrowser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aiqing.kaiheiba.imageloader.ImageLoader;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.aiqing.kaiheiba.widget.BottomAnimDialog;
import com.andbase.tractor.utils.Util;
import com.buyi.huxq17.serviceagency.ServiceAgency;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import pub.devrel.easypermissions.EasyPermissions;

import static com.aiqing.imagepicker.utils.MediaUtils.createNewFile;
import static com.aiqing.imagepicker.utils.MediaUtils.fileScan;

public class ImageBrowserPagerAdapter extends PagerAdapter implements View.OnLongClickListener {
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
        photoView.setTag(position);
        photoView.setOnLongClickListener(this);
        return photoView;
    }

    @Override
    public boolean onLongClick(final View v) {
        final int position = (int) v.getTag();
        final Context context = v.getContext();
        v.getDrawingCache();
        BottomAnimDialog bottomAnimDialog = new BottomAnimDialog(context);
        bottomAnimDialog.setClickListener(new BottomAnimDialog.BottonAnimDialogListener() {
            @Override
            public void onItemClick() {
                saveImgWithURL(context, imgs.get(position));
            }

            @Override
            public void onCancel() {

            }
        });
        bottomAnimDialog.show();
        return true;
    }

    public void saveImgWithURL(final Context context, final String url) {
        if (!EasyPermissions.hasPermissions(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                int connectTimeout = 30 * 1000; // 连接超时:30s
                int readTimeout = 1 * 1000 * 1000; // IO超时:1min
                byte[] buffer = new byte[8 * 1024]; // IO缓冲区:8KB;
                File file = createNewFile(context, new Bundle(), false);
                InputStream in = null;
                OutputStream out = null;
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setConnectTimeout(connectTimeout);
                    conn.setReadTimeout(readTimeout);
                    conn.connect();
                    in = conn.getInputStream();
                    out = new FileOutputStream(file);

                    for (; ; ) {
                        int bytes = in.read(buffer);
                        if (bytes == -1) {
                            break;
                        }
                        out.write(buffer, 0, bytes);
                    }
                    e.onNext(file);
                    conn.disconnect();
                } catch (IOException ex) {
                    file.delete();
                    e.onError(ex);
                } finally {
                    Util.closeQuietly(in);
                    Util.closeQuietly(out);
                }
            }
        })
                .compose(RxSchedulers.<File>compose())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        fileScan(context, file.getAbsolutePath());
                        Toast.makeText(context, "下载成功,存在" + file.getParentFile().getAbsolutePath() + "路径下", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

    }
}
