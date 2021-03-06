package com.aiqing.kaiheiba.weex;

import android.content.Intent;

import com.aiqing.kaiheiba.imagebrowser.ImageBrowserActivity;
import com.aiqing.kaiheiba.login.LoginAct;
import com.aiqing.kaiheiba.personal.download.MyBusinessInfLocal;
import com.aiqing.kaiheiba.personal.download.MyDownloadAct;
import com.aiqing.kaiheiba.personal.invite.InviteFriendAct;
import com.aiqing.kaiheiba.personal.profile.EditPersonProfileAct;
import com.aiqing.kaiheiba.personal.relationship.MyFansAct;
import com.aiqing.kaiheiba.personal.relationship.MyFollowAct;
import com.aiqing.kaiheiba.personal.wallet.ChargeAct;
import com.aiqing.kaiheiba.personal.wallet.MyWalletAct;
import com.aiqing.kaiheiba.personal.wallet.TradeRecordAct;
import com.aiqing.kaiheiba.settings.FeedbackAct;
import com.aiqing.kaiheiba.settings.SettingsAct;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import user.UserService;

public class WeexJumpModule extends WXModule {

    @JSMethod
    public void jumpTo(String type) {
        if (type.equals("1")) {
            jumpToSettings();
        } else if (type.equals("2")) {
            jumpToPersonalProfile();
        } else if (type.equals("3")) {
            jumpToMyFollow();
        } else if (type.equals("4")) {
            jumpToMyFans();
        } else if (type.equals("5")) {
            jumpToMyDowload();
        } else if (type.equals("6")) {
            jumpToMyWallet();
        } else if (type.equals("7")) {
            jumpToInviteFriend();
        } else if (type.equals("8")) {
            LoginAct.start(WXEnvironment.getApplication());
        } else if (type.equals("9")) {
            jumpTo(FeedbackAct.class);
        } else if (type.equals("10")) {
            jumpTo(ChargeAct.class);
        }
    }

    @JSMethod
    public void downLoad(JSONObject jsonObject) {
        String downloadurl = jsonObject.getString("Apk");
        String downName = jsonObject.getString("AppName");
        String downIcon = jsonObject.getString("Icon");
        MyBusinessInfLocal myBusinessInfLocal = new MyBusinessInfLocal(
                downloadurl.hashCode(), downName, downIcon, downloadurl);
        MyDownloadAct.start(WXEnvironment.getApplication(), myBusinessInfLocal);

    }

    @JSMethod
    public void viewPicture(Map<String, Object> map) {
        JSONArray imgs = (JSONArray) map.get("imgs");
        int index = 0;
        Object o = map.get("index");
        if (o != null)
            index = (int) map.get("index");
        ArrayList<String> imageList = new ArrayList<>(Arrays.asList(imgs.toArray(new String[imgs.size()])));
        ImageBrowserActivity.start(WXEnvironment.getApplication(), index, imageList);
    }

    public void jumpToPersonalProfile() {
        jumpTo(EditPersonProfileAct.class);
    }

    public void jumpToMyFollow() {
        jumpTo(MyFollowAct.class);
    }

    public void jumpToMyFans() {
        jumpTo(MyFansAct.class);
    }

    public void jumpToMyDowload() {
        jumpTo(MyDownloadAct.class);
    }

    public void jumpToMyWallet() {
        jumpTo(MyWalletAct.class);
    }

    public void jumpToTradeRecord() {
        jumpTo(TradeRecordAct.class);
    }

    public void jumpToInviteFriend() {
        jumpTo(InviteFriendAct.class);
    }

    public void jumpToSettings() {
        jumpTo(SettingsAct.class);
    }

    public void jumpToLogin() {
        jumpTo(LoginAct.class);
    }


    private void jumpTo(Class<?> activity) {
        if (!UserService.isLogin() && (activity != MyDownloadAct.class)) {
            LoginAct.start(WXEnvironment.getApplication());
//                    toggle(lastSelectedId);
            return;
        }

        Intent intent = new Intent(WXEnvironment.getApplication(), activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        WXEnvironment.getApplication().startActivity(intent);
    }
}