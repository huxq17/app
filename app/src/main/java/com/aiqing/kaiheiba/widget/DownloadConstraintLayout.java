package com.aiqing.kaiheiba.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.andbase.tractor.utils.LogUtils;

public class DownloadConstraintLayout extends ConstraintLayout {
    public DownloadConstraintLayout(Context context) {
        super(context);
    }

    public DownloadConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        LogUtils.e("onlayout1");
        super.onLayout(changed, left, top, right, bottom);
        LogUtils.e("onlayout2");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtils.e("onMeasure");
    }

    @Override
    public void requestLayout() {
        if (!isClick) {
            super.requestLayout();
            LogUtils.e("requestLayout in");
        }
        LogUtils.e("requestLayout out");
    }

    private boolean isClick = false;

    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        return super.invalidateChildInParent(location, dirty);
    }

    @Override
    public void onDescendantInvalidated(@NonNull View child, @NonNull View target) {
        super.onDescendantInvalidated(child, target);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        LogUtils.e("dispatchDraw1");
        super.dispatchDraw(canvas);
        LogUtils.e("dispatchDraw2");
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.e("down");
                isClick = true;
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.e("up  action=" + action);
//                v.performClick();
                isClick = false;
            case MotionEvent.ACTION_CANCEL:
                LogUtils.e(" cancel action=" + action);
//                v.performClick();
                break;
        }
        return super.onTouchEvent(event);
    }
}
