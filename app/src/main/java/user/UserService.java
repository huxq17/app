package user;


import android.text.TextUtils;

import com.huxq17.xprefs.XPrefs;

public class UserService {
    public static String getUserId() {
        User user = XPrefs.get(User.class);
        return user.id;
    }

    public static String getAccount() {
        User user = XPrefs.get(User.class);
        return user.accid;
    }

    public static String getIMToken() {
        User user = XPrefs.get(User.class);
        return user.imToken;
    }

    public static String isRealAuth() {
        User user = XPrefs.get(User.class);
        return user.isRealAuth;
    }

    public static String getAppKey() {
        User user = XPrefs.get(User.class);
        return user.appKey;
    }

    public static boolean isLogin() {
        User user = XPrefs.get(User.class);
        return !TextUtils.isEmpty(user.id) && !TextUtils.isEmpty(user.token);
    }

    public static String getMobile() {
        User user = XPrefs.get(User.class);
        return user.mobile;
    }


    public static String getUserToken() {
        User user = XPrefs.get(User.class);
        return user.token;
    }

    public static String getNickName() {
        User user = XPrefs.get(User.class);
        return user.nickname;
    }

    public static void setAvatar(String avatar) {
        User user = XPrefs.get(User.class);
        user.avatar = avatar;
        XPrefs.saveAll(user);
    }

    public static void setIsRealAuth(String auth) {
        User user = XPrefs.get(User.class);
        user.isRealAuth = auth;
        XPrefs.saveAll(user);
    }

    public static String getAvatar() {
        User user = XPrefs.get(User.class);
        return user.avatar;
    }

    public static void save(User user) {
        XPrefs.saveAll(user);
    }
}
