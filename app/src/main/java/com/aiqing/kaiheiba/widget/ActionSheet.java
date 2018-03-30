package com.aiqing.kaiheiba.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.utils.DensityUtil;

public class ActionSheet {
    private Dialog mDialog;

    public ActionSheet(Context context, View contentView, boolean cancelable, boolean otoCancelable) {
        if (context == null)
            return;
        mDialog = new Dialog(context);
//        mDialog = new Dialog(context, R.style.custom_dialog_type);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(contentView);
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(otoCancelable);
        Window window = mDialog.getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.width = d.getWidth();
        window.setAttributes(p);
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
//        window.setWindowAnimations(R.style.comment_popwindow_anim_style);  //添加动画
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public static Builder createBuilder(Context context) {
        return new Builder(context);
    }

    public static class Builder {

        private Context mContext;
        private String mCancelButtonTitle;
        private String[] mOtherButtonTitles;
        private boolean mCancelableOnTouchOutside = true;
        private ActionSheetListener mListener;
        ActionSheet ActionSheet = null;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setCancelButtonTitle(String title) {
            mCancelButtonTitle = title;
            return this;
        }

        public Builder setCancelButtonTitle(int strId) {
            return setCancelButtonTitle(mContext.getString(strId));
        }

        public Builder setOtherButtonTitles(String... titles) {
            mOtherButtonTitles = titles;
            return this;
        }

        public Builder setListener(ActionSheetListener listener) {
            this.mListener = listener;
            return this;
        }

        public Builder setCancelableOnTouchOutside(boolean cancelable) {
            mCancelableOnTouchOutside = cancelable;
            return this;
        }

        public ActionSheet show() {

            View view = View.inflate(mContext, R.layout.dialog_actionsheet, null);
            final ScrollView scrollLayout = view.findViewById(R.id.scroll_layout);
            final LinearLayout llayOther = view.findViewById(R.id.llay_other);
            //取消按钮
            TextView txtCancel =  view.findViewById(R.id.txt_cancel);
            txtCancel.setText(mCancelButtonTitle);
            txtCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ActionSheet != null)
                        ActionSheet.dismiss();
                }
            });
            //其他按钮
            if (mOtherButtonTitles != null && mOtherButtonTitles.length != 0) {
                for (int i = 0; i < mOtherButtonTitles.length; i++) {
                    TextView textView = new TextView(mContext);
                    textView.setText(mOtherButtonTitles[i]);

                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(view.getContext(),45)));
                    textView.setTextColor(0xff1E82FF);
                    textView.setTextSize(16);//16sp的字体大小转化成px
//                    int padding = (int) (10 * mContext.getResources().getDisplayMetrics().density + 0.5f);//10dp的padding转换成px
//                    textView.setPadding(0, padding, 0, padding);
                    textView.setGravity(Gravity.CENTER);

                    final int pos = i;
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mListener != null)
                                mListener.onOtherButtonClick(pos);
                            if (ActionSheet != null)
                                ActionSheet.dismiss();
                        }
                    });
                    llayOther.addView(textView);
                }


                /**
                 * 设置一定条数，不能再撑开，而是变成滑动
                 */
                scrollLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int minNumWhenScroll = 10;//最小可滑动条数
                        int childViewCount = llayOther.getChildCount();
                        int scrollLayoutHeight = 0;
                        int childHeight = 0;
                        if (childViewCount == 0) {
                            scrollLayoutHeight = 0;
                        } else {
                            childHeight = llayOther.getChildAt(0).getHeight();
                            if (childViewCount <= minNumWhenScroll) {
                                scrollLayoutHeight = childHeight * childViewCount;
                            } else {
                                scrollLayoutHeight = childHeight * minNumWhenScroll;
                            }
                        }
                        scrollLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, scrollLayoutHeight));
                        scrollLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });

            }
            ActionSheet = new ActionSheet(mContext, view, mCancelableOnTouchOutside, mCancelableOnTouchOutside);
            ActionSheet.show();

            return ActionSheet;
        }

    }

    public interface ActionSheetListener {
        void onOtherButtonClick(int index);
    }
}