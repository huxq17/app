package com.aiqing.kaiheiba.rxjava;

import com.aiqing.kaiheiba.App;
import com.aiqing.kaiheiba.bean.BaseResponse;
import com.aiqing.kaiheiba.common.Base;
import com.aiqing.kaiheiba.login.LoginAct;
import com.huxq17.xprefs.LogUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserver<T> implements Observer<BaseResponse<T>> {

    private static final String TAG = "BaseObserver";

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(BaseResponse<T> value) {
        LogUtils.d("onNext code=" + value.getCode() + ";msg=" + value.getMsg());
        if (value.isSuccess()) {
            T t = value.getData();
            onSuccess(t);
        } else {
            if (value.isTokenInvalidate()) {
                LoginAct.start(App.getContext());
                onFailed("token过期，请重新登录");
            } else {
                onFailed(value.getMsg());
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e("onError msg=" + e.getMessage());
        onFailed(e.getMessage());
    }

    @Override
    public void onComplete() {
    }


    protected abstract void onSuccess(T t);

    protected void onFailed(String msg) {
        Base.getInstance(App.getContext()).toast(msg);
    }
}