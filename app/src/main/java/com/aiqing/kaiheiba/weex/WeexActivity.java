package com.aiqing.kaiheiba.weex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;

import java.util.HashMap;
import java.util.Map;

public class WeexActivity extends BaseActivity implements IWXRenderListener {
    protected WXSDKInstance mWXSDKInstance;
    public static String DATA_KEY = "data_key";
    private Uri data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWXSDKInstance = new WXSDKInstance(this);
        mWXSDKInstance.registerRenderListener(this);
        if (savedInstanceState != null) {
            data = savedInstanceState.getParcelable(DATA_KEY);
        } else {
            data = getIntent().getData();
        }
//        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
//            @Override
//            public void doFrame(long frameTimeNanos) {
//
//            }
//        });
        render();
    }

    boolean isVisiableForLast = false;

    public void addOnSoftKeyBoardVisibleListener(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                //计算出可见屏幕的高度
                int displayHight = rect.bottom - rect.top;
                //获得屏幕整体的高度
                int hight = decorView.getHeight();
                //获得键盘高度
                int keyboardHeight = hight - displayHight;
                boolean visible = (double) displayHight / hight < 0.8;
                if (visible != isVisiableForLast) {
                    if (visible) {
                        if (mWXSDKInstance != null) {
                            Map<String, Object> params = new HashMap<>();
                            params.put("keyboardHeight", keyboardHeight);
                            mWXSDKInstance.fireGlobalEventCallback("SoftKeyBoardHeight", params);
                        }
                    }
//                    listener.onSoftKeyBoardVisible(visible,keyboardHeight );
                }
                isVisiableForLast = visible;
            }
        });
    }

    private void render() {
        mWXSDKInstance.renderByUrl(getPackageName(), data.toString(), null, null, WXRenderStrategy.APPEND_ASYNC);
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WeexActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATA_KEY, data);
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        setContentView(view);
        addOnSoftKeyBoardVisibleListener(this);
//        view.setFitsSystemWindows(true);
//        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {
    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
        if (instance.mEnd) return;
        setContentView(R.layout.layout_error);
        findViewById(R.id.bt_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                render();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityStop();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        data = null;
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityDestroy();
            mWXSDKInstance = null;
        }
    }

}
