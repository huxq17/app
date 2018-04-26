package com.aiqing.kaiheiba.weex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.aiqing.imagepicker.ImagePicker;
import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

public class ImagePickerModule extends WXModule implements ImagePicker.OnImagePickerListener {
    protected JSCallback callback;
    private ResponseHelper responseHelper = new ResponseHelper();
    private ImagePicker imagePicker;

    @Override
    public void onActivityCreate() {
        super.onActivityCreate();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        imagePicker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @JSMethod
    public void showImagePicker(final JSONObject options, final JSCallback callback) {
        this.callback = callback;
        imagePicker = new ImagePicker(getActivity(), this);
        imagePicker.showImagePicker(parseOptions(options));
    }

    private Bundle parseOptions(JSONObject options) {
        Bundle bundle = new Bundle();
        for (String key : options.keySet()) {
            Object value = options.get(key);
            if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof Integer) {
                bundle.putInt(key, (int) value);
            } else if (value instanceof Boolean) {
                bundle.putBoolean(key, (boolean) value);
            }
        }
        return bundle;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imagePicker.onActivityResult(requestCode, resultCode, data);
    }

    public Context getContext() {
        return mWXSDKInstance.getContext();
    }

    public
    @NonNull
    Activity getActivity() {
        return (Activity) mWXSDKInstance.getContext();
    }

    @Override
    public void onError(String error) {
        responseHelper.invokeError(callback, error);
    }

    @Override
    public void onSuccess(Bundle response) {
        for (String key : response.keySet()) {
            Object value = response.get(key);
            if (value instanceof String) {
                responseHelper.putString(key, (String) value);
            } else if (value instanceof Integer) {
                responseHelper.putInt(key, (int) value);
            } else if (value instanceof Boolean) {
                responseHelper.putBoolean(key, (boolean) value);
            }
        }
        responseHelper.invokeResponse(callback);
    }

    @Override
    public void onCancel() {
        responseHelper.invokeCancel(callback);
    }
}
