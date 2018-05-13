package com.aiqing.kaiheiba.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aiqing.kaiheiba.R;

public class BottomAnimDialog extends Dialog implements View.OnClickListener {


    private final Context mContext;


    private BottonAnimDialogListener mListener;
    private View mSave;
    private View mCancel;

    public BottomAnimDialog(Context context) {

        super(context, R.style.dialog);
        this.mContext = context;

        initView();

    }


    private void initView() {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bottom_anim_dialog_layout, null);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.y = DensityUtil.dip2px(mContext, 10); //设置居于底部的距离
            window.getDecorView().setPadding(0, 0, 0, 0);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        mSave = view.findViewById(R.id.item_save);
        mCancel = view.findViewById(R.id.item_cancel);

        mSave.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        setContentView(view);

    }


    public void setClickListener(BottonAnimDialogListener listener) {
        this.mListener = listener;
    }

    public interface BottonAnimDialogListener {

        void onItemClick();

        void onCancel();
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.item_save:
                if (mListener != null) {
                    mListener.onItemClick();
                }
                break;
            case R.id.item_cancel:
                if (mListener != null) {
                    mListener.onCancel();
                }
                break;
        }
    }

}