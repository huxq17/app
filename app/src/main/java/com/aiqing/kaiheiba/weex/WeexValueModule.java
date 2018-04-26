package com.aiqing.kaiheiba.weex;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.andbase.tractor.utils.LogUtils;
import com.andbase.tractor.utils.Util;
import com.huxq17.xprefs.SPUtils;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import pub.devrel.easypermissions.EasyPermissions;
import user.UserService;

import static com.aiqing.imagepicker.utils.MediaUtils.createNewFile;
import static com.aiqing.imagepicker.utils.MediaUtils.fileScan;

public class WeexValueModule extends WXModule {

    @JSMethod
    public void getIDAndTokenCallback(JSCallback callback) {
        Map<String, Object> map = new HashMap<>();
        String userId = UserService.getUserId();
        String usertoken = UserService.getUserToken();
//        if(TextUtils.isEmpty(userId)|| TextUtils.isEmpty(usertoken)){
//            LoginAct.start(WXEnvironment.getApplication());
//        }
        map.put("userID", userId);
        map.put("userToken", usertoken);
        callback.invoke(map);
        //callback.invoke(map);
    }

    @JSMethod
    public void getAccountIdCallback(JSCallback callback) {
//        if(TextUtils.isEmpty(userId)|| TextUtils.isEmpty(usertoken)){
//            LoginAct.start(WXEnvironment.getApplication());
//        }
        String accountId = SPUtils.getString(WXEnvironment.getApplication(), "waccountId");
        callback.invoke(accountId);
        //callback.invoke(map);
    }

    @JSMethod
    public void SaveImgWithURL(final String url) {
        if (!EasyPermissions.hasPermissions(mWXSDKInstance.getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }
        LogUtils.e("url=" + url);
//        final String url2="https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3322172181,3457997126&fm=27&gp=0.jpg";

        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                int connectTimeout = 30 * 1000; // 连接超时:30s
                int readTimeout = 1 * 1000 * 1000; // IO超时:1min
                byte[] buffer = new byte[8 * 1024]; // IO缓冲区:8KB;
                File file = createNewFile(mWXSDKInstance.getContext(), new Bundle(), false);
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
                        fileScan(mWXSDKInstance.getContext(), file.getAbsolutePath());
                        Toast.makeText(mWXSDKInstance.getContext(), "下载成功,存在" + file.getParentFile().getAbsolutePath() + "路径下", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

    }

    @JSMethod
    public void log(String str) {
        Log.e("123", str);
    }
}