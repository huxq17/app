package com.aiqing.kaiheiba.personal.invite;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.utils.DensityUtil;

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
        textView.setCompoundDrawablePadding(DensityUtil.dip2px(parent.getContext(),5));
        textView.setPadding(0,DensityUtil.dip2px(parent.getContext(),20),0,0);
        return new ViewHolder(textView);
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
