package com.aiqing.kaiheiba.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class ShareUtils {
    private static void share(Context context, String activityTitle, String msgTitle, String msgText) {

//        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        Intent intent = new Intent(Intent.ACTION_SEND);
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
//        intent.putExtra(Intent.EXTRA_STREAM, msgText);
//        intent.setType("image/*");
        intent.setType("text/plain");
//        ArrayList<Uri> imageUris = new ArrayList<Uri>();
//        File f = new File(FileManager.getImagePath(), "pic.png");
//        if (f != null && f.exists() && f.isFile()) {
//            intent.setType("image/*");
//            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
//            Uri u;
//            if (currentApiVersion < 24) {
//                u = Uri.fromFile(f);
//                imageUris.add(u);
//                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
//            } else {
//                //兼容android7.0 使用共享文件的形式
//                try {
//                    u = Uri.parse(MediaStore.Images.Media.
//                            insertImage(context.getContentResolver(), f.getAbsolutePath(), "pic.png", null));
//                    imageUris.add(u);
//                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
//        intent.putExtra(Intent.EXTRA_REFERRER, "http://www.baidu.com");
        intent.putExtra("Kdescription", msgText);
        intent.putExtra("sms_body", msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

    public static void share(Context context, String title, String url) {
//        if (url.startsWith("http")) {
//
//        } else {
//            copyFilesFromAssets(context, url);
        share(context, title, title,   url);
//        }
    }

    public static void copyFilesFromAssets(Context context, String assetsPath) {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            is = context.getAssets().open(assetsPath);
            File imageDir = FileManager.getImagePath();
            if (!imageDir.exists()) {
//                e.onError(new Exception("没有读取sd卡权限"));
                return;
            }
            File file = new File(imageDir, "pic.png");
            if (file.exists()) file.delete();
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(is);
            Utils.close(fos);
        }
    }

    private Observable<String> getPicture(final String imageUrl) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                URL url = null;
                InputStream is = null;
                FileOutputStream fos = null;
                File imageDir = FileManager.getImagePath();
                if (!imageDir.exists()) {
                    e.onError(new Exception("没有读取sd卡权限"));
                    return;
                }
                File file = new File(imageDir, "pic.jpg");
                if (file.exists()) {
                    file.delete();
                }
                try {
                    //构建图片的url地址
                    url = new URL(imageUrl);
                    //开启连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //设置超时的时间，5000毫秒即5秒
                    conn.setConnectTimeout(5000);
                    //设置获取图片的方式为GET
                    conn.setRequestMethod("GET");
                    //响应码为200，则访问成功
                    if (conn.getResponseCode() == 200) {
                        //获取连接的输入流，这个输入流就是图片的输入流
                        is = conn.getInputStream();
                        //构建一个file对象用于存储图片
                        fos = new FileOutputStream(file);
                        int len = 0;
                        byte[] buffer = new byte[1024];
                        //将输入流写入到我们定义好的文件中
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        //将缓冲刷入文件
                        fos.flush();
                        e.onNext(file.getAbsolutePath());
                    }
                } catch (Exception ex) {
                    //告诉handler，图片已经下载失败
                    ex.printStackTrace();
                    e.onError(ex);
                } finally {
                    Utils.close(is);
                    Utils.close(fos);
                }
            }
        });
        return observable;
    }

}
