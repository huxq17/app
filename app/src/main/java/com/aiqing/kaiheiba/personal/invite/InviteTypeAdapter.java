package com.aiqing.kaiheiba.personal.invite;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.utils.DensityUtil;
import com.aiqing.kaiheiba.utils.ShareUtils;

import user.UserService;

/**
 * Created by Administrator on 2018/3/21.
 */

public class InviteTypeAdapter extends RecyclerView.Adapter<InviteTypeAdapter.ViewHolder> {
    private InviteType[] idlist;

    public void setData(InviteType[] list) {
        idlist = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(parent.getContext().getResources().getColor(R.color.contentTextColor));
        textView.setTextSize(16);
        textView.setCompoundDrawablePadding(DensityUtil.dip2px(parent.getContext(), 5));
        textView.setPadding(0, DensityUtil.dip2px(parent.getContext(), 20), 0, 0);
        return new ViewHolder(textView);
    }

    String code;

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView imageView = (TextView) holder.itemView;
        Drawable top = imageView.getContext().getResources().getDrawable(idlist[position].resId);

        imageView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
        imageView.setText(idlist[position].name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(code))
                    ShareUtils.share(v.getContext(), "邀请好友", "您的好友" + UserService.getNickName() + "在“一起开黑吧”——专注游戏社交的移动APP邀你一起开黑，" +
                            "点击就可加入战斗！请复制本链接在浏览器内打开：" + "http://weex.17kaiheiba.com/share/share.html?code=" + code);
            }
        });

    }

    @Override
    public int getItemCount() {
        return idlist.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
