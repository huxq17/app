package com.aiqing.kaiheiba.personal.blocklist;

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
import com.aiqing.kaiheiba.api.OssToken;
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

public class BlockListAdapter extends BaseRecyclerViewAdapter<RelationshipApi.BlockBean.DataBean, BlockListAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fansAvatar;
        TextView fansName;
        ImageView fansAgender;
        TextView fansDes;
        Button isFollowFans;
        View.OnClickListener onClickListener;
        BlockListAdapter followAdapter;

        public ViewHolder(View view, BlockListAdapter adapter) {
            super(view);
            followAdapter = adapter;
            fansAvatar = view.findViewById(R.id.fans_avatar);
            fansName = view.findViewById(R.id.fans_name);
            fansAgender = view.findViewById(R.id.fans_gender);
            fansDes = view.findViewById(R.id.fans_des);
            isFollowFans = view.findViewById(R.id.fans_follow);
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    int followid = followAdapter.getData(position).getID();
                    ApiManager.INSTANCE.getApi(RelationshipApi.class).unBlock(followid).compose(RxSchedulers.<BaseResponse<Object>>compose())
                            .subscribe(new BaseObserver<Object>() {
                                @Override
                                protected void onSuccess(Object o) {
                                    followAdapter.deleteData(position);
                                }
                            });
                }
            };
            isFollowFans.setOnClickListener(onClickListener);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_fans, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RelationshipApi.BlockBean.DataBean dataBean = getData(position);
        AccountBean accountBean = dataBean.getTargetAccount();
        holder.fansName.setText(accountBean.getNickname());
        holder.fansDes.setText(accountBean.getSign());
        String genderS = accountBean.getGender();
        if (genderS == null) {
            return;
        }
        int gender = Integer.parseInt(genderS);
        if (gender == 0) {
            holder.fansAgender.setImageResource(R.mipmap.prof_unknow_s);
        } else if (gender == 1) {
            holder.fansAgender.setImageResource(R.mipmap.prof_male_s);
        } else if (gender == 2) {
            holder.fansAgender.setImageResource(R.mipmap.prof_female_s);
        }
        String avatarUrl = accountBean.getAvatar();
        if (!TextUtils.isEmpty(avatarUrl)) {
            ServiceAgency.getService(ImageLoader.class).loadImage(OssToken.Client.OSSDomain + accountBean.getAvatar(), holder.fansAvatar);
        } else {
            holder.fansAvatar.setImageResource(R.mipmap.avatar_default);
        }
//        holder.isFollowFans
        boolean isFollow = true;
        Context context = holder.fansName.getContext();
        int round = DensityUtil.dip2px(context, 5);
        int strokenWidth = DensityUtil.dip2px(context, 1);
        int strokenColor;
        int background = Color.WHITE;
        String btText;
        int textColor;
        strokenColor = Color.parseColor("#F48432");
        btText = "取消屏蔽";
        textColor = Color.parseColor("#F48432");
        Drawable drawable = Utils.setStrokenBg(strokenWidth, round, strokenColor, background);
        holder.isFollowFans.setBackgroundDrawable(drawable);
        holder.isFollowFans.setText(btText);
        holder.isFollowFans.setTextColor(textColor);
        holder.isFollowFans.setTextSize(14);
        holder.isFollowFans.setTag(position);
    }
}
