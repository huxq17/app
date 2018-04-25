package com.aiqing.kaiheiba.weex;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.OssToken;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class WeexUploadModule extends WXModule {
    private int uploadedSize;
    JSONArray imgs;
    JSCallback jsCallback;
    List<String> list;

    @JSMethod
    public void uploadImg(Map<String, Object> map, JSCallback callback) {
        imgs = (JSONArray) map.get("imgDataArr");
        String type = (String) map.get("type");
        if (imgs.size() == 0) {
            return;
        }
        uploadedSize = 0;
        list = new ArrayList<>();
        for (int i = 0; i < imgs.size(); i++) {
            com.alibaba.fastjson.JSONObject img = (JSONObject) imgs.get(i);
            Uri uri = Uri.parse(img.getString("path"));
            File file = new File(getPath(uri));
            upload(type, file);
        }
        jsCallback = callback;
    }

    private void upload(final String type, final File file) {
        ApiManager.INSTANCE.getApi(OssToken.Api.class).getToken()
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<OssToken.Bean, ObservableSource<PutObjectResult>>() {
                    @Override
                    public ObservableSource<PutObjectResult> apply(OssToken.Bean bean) throws Exception {
                        final OssToken.OSSBean ossBean = bean.getData();
                        return updateAvatar(type, ossBean, file);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PutObjectResult>() {
                    @Override
                    public void accept(PutObjectResult putObjectResult) throws Exception {
                        String avatarUrl = OssToken.Client.getObjectKey(type, file.getName());
                        list.add(avatarUrl);
                        uploadedSize++;
                        if (uploadedSize == imgs.size()) {
                            jsCallback.invoke(list.toArray());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(WXEnvironment.getApplication(), "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public Observable<PutObjectResult> updateAvatar(final String type, final OssToken.OSSBean ossBean, final File file) {
        return Observable.create(new ObservableOnSubscribe<PutObjectResult>() {
            @Override
            public void subscribe(ObservableEmitter<PutObjectResult> emitter) throws Exception {
                OSS oss = OssToken.Client.init(WXEnvironment.getApplication(), ossBean);
                PutObjectRequest put = new PutObjectRequest("aiqing-lianyun", OssToken.Client.getObjectKey(type, file.getName()), file.getPath());
                try {
                    PutObjectResult putResult = oss.putObject(put);
                    emitter.onNext(putResult);
                } catch (ClientException e) {
                    emitter.onError(e);
                } catch (ServiceException e) {
                    emitter.onError(e);
                    // 服务异常
                    Log.e("RequestId", e.getRequestId());
                    Log.e("ErrorCode", e.getErrorCode());
                    Log.e("HostId", e.getHostId());
                    Log.e("RawMessage", e.getRawMessage());
                }
            }
        });

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = WXEnvironment.getApplication().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @JSMethod
    public void log(String str) {
        Log.e("123", str);
    }
}