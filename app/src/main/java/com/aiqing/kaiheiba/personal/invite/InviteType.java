package com.aiqing.kaiheiba.personal.invite;


import com.aiqing.kaiheiba.R;

public enum InviteType {
    WX("微信", R.mipmap.iv_invite_wx),
    CIRCLE("朋友圈", R.mipmap.iv_invite_circle),
    QQ("QQ", R.mipmap.iv_invite_qq),
    ZONE("QQ空间", R.mipmap.iv_invite_zone),
    WB("微博", R.mipmap.iv_invite_wb);
    String name;
    int resId;

    InviteType(String name, int resId) {
        this.name = name;
        this.resId = resId;
    }
}
