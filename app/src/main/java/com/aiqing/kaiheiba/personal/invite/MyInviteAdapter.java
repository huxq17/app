package com.aiqing.kaiheiba.personal.invite;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.bean.AccountBean;

import java.util.ArrayList;
import java.util.List;


public class MyInviteAdapter extends RecyclerView.Adapter<MyInviteAdapter.ViewHolder> {
    private List<AccountBean.InviteBean> mDatas = new ArrayList<>();

    public void setData(List<AccountBean.InviteBean> list) {
        mDatas.clear();
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_invite_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AccountBean.InviteBean bean = mDatas.get(position);
        holder.tvAccount.setText(bean.mobile);
        holder.tvData.setText(bean.createAt);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccount;
        TextView tvData;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAccount = itemView.findViewById(R.id.v_item_myinvite_account);
            tvData = itemView.findViewById(R.id.v_item_myinvite_register_data);
        }
    }
}
