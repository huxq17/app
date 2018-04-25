package com.aiqing.kaiheiba.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by Administrator on 2018/4/25.
 */

public class DatePicker extends NumberPicker {
    public DatePicker(Context context) {
        super(context);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    public void updateView(View view) {
        if (view instanceof EditText) {
            //这里修改字体的属性
//            ((EditText) view).setTextColor(Color.parseColor("#BAA785"));
//            ((EditText) view).setTextSize();
        }
    }
}
