package com.aiqing.kaiheiba.neteasyim.contact.activity;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.bean.AccountBean;
import com.aiqing.kaiheiba.imageloader.ImageLoader;
import com.buyi.huxq17.serviceagency.ServiceAgency;
import com.huxq17.xprefs.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private List<AccountBean> mDatas = new ArrayList<>();

    public void setData(List<AccountBean> datas) {
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
            fansAvatar = view.findViewById(R.id.fans_avatar);
            fansName = view.findViewById(R.id.fans_name);
            fansAgender = view.findViewById(R.id.fans_gender);
            fansDes = view.findViewById(R.id.fans_des);
            isFollowFans = view.findViewById(R.id.fans_follow);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        AccountBean accountBean = mDatas.get(position);
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
        String avatarurl = accountBean.getAvatar();
        if (!TextUtils.isEmpty(avatarurl)) {
            ServiceAgency.getService(ImageLoader.class).loadImage(accountBean.getAvatar(), holder.fansAvatar);
        }
        holder.itemView.setTag(accountBean.getAccid());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = (String) v.getTag();
                LogUtils.e("account="+account);
                if (!TextUtils.isEmpty(account))
                    UserProfileActivity.start(holder.itemView.getContext(), account);
            }
        });
//        holder.isFollowFans
//        boolean isFollow = true;
//        Context context = holder.fansName.getContext();
//        int round = DensityUtil.dip2px(context, 5);
//        int strokenWidth = DensityUtil.dip2px(context, 1);
//        int strokenColor;
//        int background = Color.WHITE;
//        String btText;
//        int textColor;
//        strokenColor = Color.parseColor("#F48432");
//        btText = "取消关注";
//        textColor = Color.parseColor("#F48432");
//        Drawable drawable = Utils.setStrokenBg(strokenWidth, round, strokenColor, background);
//        holder.isFollowFans.setBackgroundDrawable(drawable);
//        holder.isFollowFans.setText(btText);
//        holder.isFollowFans.setTextColor(textColor);
//        holder.isFollowFans.setTextSize(14);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}