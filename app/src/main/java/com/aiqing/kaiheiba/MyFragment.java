package com.aiqing.kaiheiba;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aiqing.kaiheiba.common.BaseFragment;
import com.aiqing.kaiheiba.login.LoginAct;
import com.aiqing.kaiheiba.neteasyim.IMActivity;
import com.aiqing.kaiheiba.personal.download.MyDownloadAct;
import com.aiqing.kaiheiba.personal.invite.InviteFriendAct;
import com.aiqing.kaiheiba.personal.profile.EditPersonProfileAct;
import com.aiqing.kaiheiba.personal.relationship.MyFansAct;
import com.aiqing.kaiheiba.personal.relationship.MyFollowAct;
import com.aiqing.kaiheiba.personal.wallet.MyWalletAct;
import com.aiqing.kaiheiba.personal.wallet.TradeRecordAct;
import com.aiqing.kaiheiba.settings.SettingsAct;


public class MyFragment extends BaseFragment {
    public MyFragment() {
    }

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        view.findViewById(R.id.jumpToIM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToIM(v);
            }
        });
        view.findViewById(R.id.jumpToPersonalProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToPersonalProfile(v);
            }
        });
        view.findViewById(R.id.jumpToMyFollow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToMyFollow(v);
            }
        });
        view.findViewById(R.id.jumpToMyFans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToMyFans(v);
            }
        });
        view.findViewById(R.id.jumpToMyDowload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToMyDowload(v);
            }
        });
        view.findViewById(R.id.jumpToMyWallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToMyWallet(v);
            }
        });
        view.findViewById(R.id.jumpToTradeRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToTradeRecord(v);
            }
        });
        view.findViewById(R.id.jumpToInviteFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToInviteFriend(v);
            }
        });
        view.findViewById(R.id.jumpToSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToSettings(v);
            }
        });
        view.findViewById(R.id.jumpToLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToLogin(v);
            }
        });
        return view;
    }

    public void jumpToPersonalProfile(View v) {
        jumpTo(EditPersonProfileAct.class);
    }

    public void jumpToMyFollow(View v) {
        jumpTo(MyFollowAct.class);
    }

    public void jumpToMyFans(View v) {
        jumpTo(MyFansAct.class);
    }

    public void jumpToMyDowload(View v) {
        jumpTo(MyDownloadAct.class);
    }

    public void jumpToMyWallet(View v) {
        jumpTo(MyWalletAct.class);
    }

    public void jumpToTradeRecord(View v) {
        jumpTo(TradeRecordAct.class);
    }

    public void jumpToInviteFriend(View v) {
        jumpTo(InviteFriendAct.class);
    }

    public void jumpToSettings(View v) {
        jumpTo(SettingsAct.class);
    }

    public void jumpToLogin(View v) {
        jumpTo(LoginAct.class);
    }

    public void jumpToIM(View v) {
        jumpTo(IMActivity.class);
    }

    private void jumpTo(Class<?> activity) {
        Intent intent = new Intent(getActivity(), activity);
        startActivity(intent);
    }
}
