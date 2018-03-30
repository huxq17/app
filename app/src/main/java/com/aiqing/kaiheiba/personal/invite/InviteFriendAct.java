package com.aiqing.kaiheiba.personal.invite;


import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.UserApi;
import com.aiqing.kaiheiba.bean.AccountBean;
import com.aiqing.kaiheiba.bean.BaseResponse;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;

import java.util.ArrayList;
import java.util.List;

import user.UserService;

public class InviteFriendAct extends BaseActivity {
    RecyclerView mInviteListView;
    InviteTypeAdapter mAdapter;
    RadioGroup selectTab;
    View myInviteLayout;

    RecyclerView myInviteListView;
    MyInviteAdapter MyInviteAdapter;
    TextView tvInviteNum, tvMyAccount, tvInviteCode, tvCopy;
    List<AccountBean.InviteBean> inviteList = new ArrayList<>();
    String inviteCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("邀请好友");
        setContentView(R.layout.activity_invite_friend);
        hideShadow();
        mInviteListView = findViewById(R.id.v_invite_type_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mInviteListView.setLayoutManager(layoutManager);
        mAdapter = new InviteTypeAdapter();
        InviteType[] idlist = {InviteType.WX, InviteType.CIRCLE, InviteType.QQ, InviteType.ZONE, InviteType.WB};
        mInviteListView.setAdapter(mAdapter);
        mAdapter.setData(idlist);

        selectTab = findViewById(R.id.tab_select);
        myInviteLayout = findViewById(R.id.layout_my_invite);
        selectTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mInviteListView.setVisibility(checkedId == R.id.rb_invite_type ? View.VISIBLE : View.GONE);
                myInviteLayout.setVisibility(checkedId != R.id.rb_invite_type ? View.VISIBLE : View.GONE);
            }
        });
        selectTab.check(R.id.rb_invite_type);

        myInviteListView = findViewById(R.id.v_my_invite_list);
        myInviteListView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        myInviteListView.setLayoutManager(linearLayoutManager);
        MyInviteAdapter = new MyInviteAdapter();
        myInviteListView.setAdapter(MyInviteAdapter);
        tvInviteNum = findViewById(R.id.v_invite_num);
        tvMyAccount = findViewById(R.id.v_my_account);
        tvInviteCode = findViewById(R.id.v_invite_code);
        tvCopy = findViewById(R.id.v_copy_code);
        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) InviteFriendAct.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(inviteCode);
                toast("已经复制到剪切板");
            }
        });
        ApiManager.INSTANCE.getApi(UserApi.class).getInvitors()
                .compose(RxSchedulers.<BaseResponse<AccountBean>>compose())
                .subscribe(new BaseObserver<AccountBean>() {
                    @Override
                    protected void onSuccess(AccountBean accountBean) {
                        List<AccountBean.InviteBean> list = accountBean.invitors;
                        if (list != null) {
                            inviteList = list;
                            MyInviteAdapter.setData(inviteList);
                        }
                        tvInviteNum.setText(""+inviteList.size());
                        inviteCode = accountBean.getInviteCode();
                        tvInviteCode.setText("我的邀请码：" + inviteCode);
                        tvMyAccount.setText(UserService.getUserId());

                    }
                });
    }

}
