package com.aiqing.kaiheiba.personal.wallet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.common.BaseRecyclerViewAdapter;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.aiqing.kaiheiba.utils.DensityUtil;
import com.aiqing.kaiheiba.utils.Utils;
import com.aiyou.sdk.LGSDK;

import io.reactivex.Observable;

public class ChargeListAdapter extends BaseRecyclerViewAdapter<ChargeListBean.DataBean.AndroidBean, ChargeListAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLingShi;
        TextView tvLinshiNum;
        Button btBuy;
        View.OnClickListener onClickListener;
        ChargeListAdapter followAdapter;
        Context context;

        public ViewHolder(View view, ChargeListAdapter adapter) {
            super(view);
            context = view.getContext();
            followAdapter = adapter;
            ivLingShi = view.findViewById(R.id.icon_lingshi);
            tvLinshiNum = view.findViewById(R.id.lingshi_num);
            btBuy = view.findViewById(R.id.bt_buy_lingshi);
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (v == btBuy) {
                        Observable<WalletApi.OrderBean> createOrder = ApiManager.INSTANCE.getApi(WalletApi.class).createOrder(followAdapter.getData(position).getID());
                        createOrder.compose(RxSchedulers.<WalletApi.OrderBean>compose())
                                .subscribe(new BaseObserver<WalletApi.OrderBean.DataBean>() {
                                    @Override
                                    protected void onSuccess(WalletApi.OrderBean.DataBean dataBean) {
                                        com.aiyou.sdk.Constants.Constants.APP_ID = dataBean.getApp_id();
                                        LGSDK.LGPay(context, Integer.parseInt(dataBean.getPrice()), dataBean.getSubject(), dataBean.getOrder_name(), dataBean.getApp_order_id(), dataBean.getApp_user_id(),
                                                dataBean.getApp_username(), dataBean.getApp_notify_url(), dataBean.getApp_attach(), dataBean.getSign());
                                    }
                                });
                    }
                }
            };
            btBuy.setOnClickListener(onClickListener);
            view.setOnClickListener(onClickListener);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_charge_list, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChargeListBean.DataBean.AndroidBean dataBean = getData(position);
        Context context = holder.context;
        holder.tvLinshiNum.setText(dataBean.getNumber());
        int round = DensityUtil.dip2px(context, 5);
        int strokenWidth = DensityUtil.dip2px(context, 1);
        int background = Color.TRANSPARENT;
        int textColor;
        int strokenColor = Color.parseColor("#119AD0");
        textColor = Color.parseColor("#119AD0");
        Drawable drawable = Utils.setStrokenBg(strokenWidth, round, strokenColor, background);
        holder.btBuy.setBackgroundDrawable(drawable);
        holder.btBuy.setTextColor(textColor);
        holder.btBuy.setTextSize(14);
    }
}
