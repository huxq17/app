package com.aiqing.kaiheiba.personal.wallet;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;

import java.util.ArrayList;
import java.util.List;

public class TradeRecordAdapter extends RecyclerView.Adapter<TradeRecordAdapter.ViewHolder> {
    private List<WalletApi.RecordBean.DataBean.ResultBean> mDatas = new ArrayList<>();

    public void setData(List<WalletApi.RecordBean.DataBean.ResultBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tradeName;
        TextView tvDate;
        TextView tvMoney;

        public ViewHolder(View view) {
            super(view);
            tradeName = view.findViewById(R.id.v_trade_name);
            tvDate = view.findViewById(R.id.v_trade_time);
            tvMoney = view.findViewById(R.id.v_trade_money);
        }
    }

    @Override
    public TradeRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_trade_item, parent, false);
        return new TradeRecordAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TradeRecordAdapter.ViewHolder holder, int position) {
        WalletApi.RecordBean.DataBean.ResultBean resultBean = mDatas.get(position);
        holder.tradeName.setText(resultBean.getDesc());
        holder.tvDate.setText(resultBean.getCreatedAt());
        holder.tvMoney.setText(resultBean.getAmount());
//        holder.fansName.setText(fans.name);
//        holder.fansDes.setText(fans.description);
//        holder.fansAgender.setImageResource(fans.gender.getResId());
////        holder.btBuy
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
//        holder.btBuy.setBackgroundDrawable(drawable);
//        holder.btBuy.setText(btText);
//        holder.btBuy.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}