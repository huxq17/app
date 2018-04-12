package com.aiqing.kaiheiba.neteasyim;

import android.app.Activity;

import com.aiqing.kaiheiba.login.LoginAct;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;

public class UserStatusObserver implements Observer<StatusCode> {
    private Activity activity;

    public UserStatusObserver(Activity activity) {
        this.activity = activity;
    }

    public void register() {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(this, true);
    }

    public void unregister() {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(this, false);
    }

    @Override
    public void onEvent(StatusCode code) {
        if (code.wontAutoLogin()) {
            LoginAct.start(activity, true);
        } else {
            if (code == StatusCode.NET_BROKEN) {
            } else if (code == StatusCode.UNLOGIN) {
            } else if (code == StatusCode.CONNECTING) {
            } else if (code == StatusCode.LOGINING) {
            } else {
            }
        }
    }
}
