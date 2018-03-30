package com.aiqing.kaiheiba.personal.wallet;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;

import java.util.ArrayList;
import java.util.List;

public class TradeRecordAdapter extends RecyclerView.Adapter<TradeRecordAdapter.ViewHolder> {
    private List<TradeBean> mDatas = new ArrayList<>();

    public void setData(List<TradeBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fansAvatar;
        TextView fansName;
        ImageView fansAgender;
        TextView fansDes;
        Button isFollowFans;

        public ViewHolder(View view) {
            super(view);
//            fansAvatar = view.findViewById(R.id.fans_avatar);
//            fansName = view.findViewById(R.id.fans_name);
//            fansAgender = view.findViewById(R.id.fans_gender);
//            fansDes = view.findViewById(R.id.fans_des);
//            isFollowFans = view.findViewById(R.id.fans_follow);
        }
    }

    @Override
    public TradeRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_trade_item, parent, false);
        return new TradeRecordAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TradeRecordAdapter.ViewHolder holder, int position) {
//        FansBean fans = mDatas.get(position);
//        holder.fansName.setText(fans.name);
//        holder.fansDes.setText(fans.description);
//        holder.fansAgender.setImageResource(fans.gender.getResId());
////        holder.isFollowFans
//        boolean isFollow = fans.isFollow;
//        Context context = holder.fansName.getContext();
//        int round = DensityUtil.dip2px(context, 5);
//        int strokenWidth = DensityUtil.dip2px(context, 1);
//        int strokenColor;
//        int background = Color.WHITE;
//        String btText;
//        int textColor;
//        if (isFollow) {
//            strokenColor = Color.parseColor("#818181");
//            btText = "已关注";
//            textColor = context.getResources().getColor(R.color.contentTextColor);
//        } else {
//            strokenColor = Color.parseColor("#0B9AD4");
//            btText = "+关注";
//            textColor = Color.parseColor("#0B9AD4");
//        }
//        Drawable drawable = Utils.setStrokenBg(strokenWidth, round, strokenColor, background);
//        holder.isFollowFans.setBackgroundDrawable(drawable);
//        holder.isFollowFans.setText(btText);
//        holder.isFollowFans.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}