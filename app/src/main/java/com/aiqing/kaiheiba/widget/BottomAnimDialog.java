package com.aiqing.kaiheiba.widget;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiyou.toolkit.common.IDFactory;

import java.util.ArrayList;
import java.util.List;

public class BottomAnimDialog extends Dialog implements View.OnClickListener {
    private final Activity activity;

    private final List<Item> items;

    public BottomAnimDialog(Activity activity) {

        super(activity, R.style.dialog);
        this.activity = activity;
        items = new ArrayList<>();
    }

    public BottomAnimDialog addItem(Item item) {
        items.add(item);
        return this;
    }

    public void build() {
        LinearLayout rootView = new LinearLayout(activity);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setBackgroundColor(Color.WHITE);

        parseItem(rootView);
        configWindow();
        setContentView(rootView);
    }

    private void parseItem(LinearLayout rootView) {
        for (Item item : items) {
            String text = item.text;
            TextView textView = new TextView(activity);
            textView.setText(text);
            textView.setGravity(item.gravity);
            textView.setTextSize(item.textSize);
            textView.setId(item.id);
            textView.setOnClickListener(this);
            textView.setTag(item);
            rootView.addView(textView, new LinearLayout.LayoutParams(item.width, item.height));
        }
    }

    private void configWindow() {
        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.y = DensityUtil.dip2px(mContext, 10);
            window.getDecorView().setPadding(0, 0, 0, 0);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
    }
    public static interface OnClickListener {
        void onClick();
    }
    public static class Item {
        int id;
        String text;
        int gravity;
        int textSize;
        int height;
        int width;
        OnClickListener onClickListener;

        public Item(String text, OnClickListener onClickListener) {
            this.text = text;
            gravity = Gravity.CENTER;
            id = IDFactory.generateId();
            this.textSize = 16;
            width = ViewGroup.LayoutParams.MATCH_PARENT;
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
            this.onClickListener = onClickListener;
        }

        public Item gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Item textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Item width(int width) {
            this.width = width;
            return this;
        }

        public Item height(int height) {
            this.height = height;
            return this;
        }
    }

    public interface BottomAnimDialogListener {

        void onItemClick();

        void onCancel();
    }

    @Override
    public void onClick(View v) {
        dismiss();
        Object tag = v.getTag();
        if (tag == null || !(tag instanceof Item)) return;
        Item item = (Item) tag;
        if (item != null) {
            item.onClickListener.onClick();
        }
    }

    @Override
    public void show() {
        if (activity.isFinishing()) {
            return;
        }
        super.show();
    }
}