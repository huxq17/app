package com.aiqing.kaiheiba.rxjava;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.aiqing.kaiheiba.App;
import com.aiqing.kaiheiba.bean.BaseResponse;
import com.aiqing.kaiheiba.common.Base;
import com.aiqing.kaiheiba.login.LoginAct;
import com.huxq17.xprefs.LogUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserver<T> implements Observer<BaseResponse<T>> {

    private static final String TAG = "BaseObserver";
    private ProgressDialog mDialog;
    private Disposable mDisposable;
    private Context mContext;

    public BaseObserver() {
    }

    public BaseObserver(Context context) {
        this.mContext = context;
        mDialog = new ProgressDialog(context);
        if (mDialog != null) {
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dispose();
                }
            });
        }
        showDialg();
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
    }

    @Override
    public void onNext(BaseResponse<T> value) {
        LogUtils.d("onNext code=" + value.getCode() + ";msg=" + value.getMsg());
        if (value.isSuccess()) {
            closeDialog();
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
        closeDialog();
        LogUtils.e("onError msg=" + e.getMessage());
        onFailed(e.getMessage());
    }

    @Override
    public void onComplete() {
        closeDialog();
    }

    private void closeDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void showDialg() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void dispose() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    protected abstract void onSuccess(T t);

    protected void onFailed(String msg) {
        Base.getInstance(App.getContext()).toast(msg);
    }
}