package com.aiqing.kaiheiba.personal.relationship;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.RelationshipApi;
import com.aiqing.kaiheiba.bean.AccountBean;
import com.aiqing.kaiheiba.bean.BaseResponse;
import com.aiqing.kaiheiba.common.BaseRecyclerViewAdapter;
import com.aiqing.kaiheiba.imageloader.ImageLoader;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.aiqing.kaiheiba.utils.DensityUtil;
import com.aiqing.kaiheiba.utils.Utils;
import com.buyi.huxq17.serviceagency.ServiceAgency;

public class FansAdapter extends BaseRecyclerViewAdapter<RelationshipApi.Bean.DataBean, FansAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fansAvatar;
        TextView fansName;
        ImageView fansAgender;
        TextView fansDes;
        Button isFollowFans;

        public ViewHolder(View view) {
            super(view);
            fansAvatar = view.findViewById(R.id.fans_avatar);
            fansName = view.findViewById(R.id.fans_name);
            fansAgender = view.findViewById(R.id.fans_gender);
            fansDes = view.findViewById(R.id.fans_des);
            isFollowFans = view.findViewById(R.id.fans_follow);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_fans, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RelationshipApi.Bean.DataBean dataBean = getData(position);
        AccountBean accountBean = dataBean.getAccount();
        holder.fansName.setText(accountBean.getNickname());
        holder.fansDes.setText(accountBean.getSign());

        int gender = Integer.parseInt(accountBean.getGender());
        if (gender == 0) {
            holder.fansAgender.setImageResource(R.mipmap.prof_unknow_);
        } else if (gender == 1) {
            holder.fansAgender.setImageResource(R.mipmap.prof_male_n);
        } else if (gender == 2) {
            holder.fansAgender.setImageResource(R.mipmap.prof_female_n);
        }
        String avatarUrl = accountBean.getAvatar();
        if (!TextUtils.isEmpty(avatarUrl)) {
            ServiceAgency.getService(ImageLoader.class).loadImage(accountBean.getAvatar(), holder.fansAvatar);
        } else {
            holder.fansAvatar.setImageResource(R.mipmap.avatar_default);
        }
        String followStr = accountBean.getIsFollowed();
        final boolean isFollow;
        if ("0".equals(followStr)) {
            isFollow = false;
        } else if ("1".equals(followStr)) {
            isFollow = true;
        } else {
            isFollow = false;
        }
        Context context = holder.fansName.getContext();
        int round = DensityUtil.dip2px(context, 5);
        int strokenWidth = DensityUtil.dip2px(context, 1);
        int strokenColor;
        int background = Color.WHITE;
        String btText;
        int textColor;
        if (isFollow) {
            strokenColor = Color.parseColor("#818181");
            btText = "已关注";
            textColor = context.getResources().getColor(R.color.contentTextColor);
        } else {
            strokenColor = Color.parseColor("#0B9AD4");
            btText = "+关注";
            textColor = Color.parseColor("#0B9AD4");
        }
        Drawable drawable = Utils.setStrokenBg(strokenWidth, round, strokenColor, background);
        holder.isFollowFans.setBackgroundDrawable(drawable);
        holder.isFollowFans.setText(btText);
        holder.isFollowFans.setTextColor(textColor);
        holder.isFollowFans.setTag(position);
        if (!isFollow) {
            holder.isFollowFans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = (int) v.getTag();
                    final RelationshipApi.Bean.DataBean dataBean = getData(position);
                    int followid = dataBean.getAccountFollowId();
                    ApiManager.INSTANCE.getApi(RelationshipApi.class).follow(followid)
                            .compose(RxSchedulers.<BaseResponse<Object>>compose())
                            .subscribe(new BaseObserver<Object>() {
                                @Override
                                protected void onSuccess(Object o) {
                                    dataBean.getAccount().setIsFollowed("1");
                                    modifyData(position, dataBean);
                                }
                            });
                }
            });
        }else{
            holder.isFollowFans.setOnClickListener(null);
        }
    }

}
