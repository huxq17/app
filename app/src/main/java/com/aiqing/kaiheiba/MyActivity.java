package com.aiqing.kaiheiba;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aiqing.kaiheiba.common.BaseActivity;
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

import pub.devrel.easypermissions.AfterPermissionGranted;

public class MyActivity extends BaseActivity {
    private final int RC_BASIC_PERMISSIONS = 1;
    /**
     * 基本权限管理
     */
    private final String[] BASIC_PERMISSIONS = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    @AfterPermissionGranted(RC_BASIC_PERMISSIONS)
    private void onBasicPermissionGranted() {
//        takePhoto();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MyActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
