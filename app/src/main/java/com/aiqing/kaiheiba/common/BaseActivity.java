package com.aiqing.kaiheiba.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.utils.Apk;

import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    TextView navTitle;
    View navShadow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        this.getWindow().getDecorView().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        loadBase(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ImageView navBack = findViewById(R.id.nav_back);
        navShadow = findViewById(R.id.nav_bottom_shadow);
        if (navBack == null || navShadow == null) return;
        navBack.setOnClickListener(this);
        navTitle = findViewById(R.id.nav_title);
        navTitle.setText(getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void hideShadow() {
        navShadow.setVisibility(View.GONE);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (navTitle != null) {
            navTitle.setText(title);
        }
    }

    public void log(String msg) {
        Log.e("tag", msg);
    }

    public void onClickNavBack() {
        finish();
    }

    public void onTitleClick() {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Apk.INSTANCE.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.nav_back:
                onClickNavBack();
                break;
            case R.id.nav_title:
                onTitleClick();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    public void loadBase(Context context) {
        mBase = Base.getInstance(context);
    }

    private Base mBase;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && mBase != null && mBase.isHideInput(view, ev)) {
                mBase.HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 格式化字符串
     *
     * @param format
     * @param args
     */
    public String format(String format, Object... args) {
        if (mBase != null) {
            return mBase.format(format, args);
        } else {
            return null;
        }
    }

    public void setText(Object obj, String str) {
        if (mBase != null) {
            mBase.setText(obj, str);
        }
    }

    /**
     * 获取edittext，textView,checkbox和button的文字
     *
     * @param obj
     * @return
     */
    public String getText(Object obj) {
        if (mBase != null) {
            return mBase.getText(obj);
        } else {
            return "";
        }
    }

    public boolean isEmpty(Object obj) {
        if (mBase != null) {
            return mBase.isEmpty(obj);
        } else {
            return true;
        }
    }

    public boolean isEmpty(String str) {
        return mBase != null ? mBase.isEmpty(str) : true;
    }

    public void toast(String msg) {
        if (mBase != null) {
            mBase.toast(msg);
        }
    }

    public void toastAll(String msg) {
        if (mBase != null) {
            mBase.toastAll(msg);
        }
    }

    public void toastL(String msg) {
        if (mBase != null) {
            mBase.toastL(msg);
        }
    }

    public void toastAllL(String msg) {
        if (mBase != null) {
            mBase.toastAllL(msg);
        }
    }
}
