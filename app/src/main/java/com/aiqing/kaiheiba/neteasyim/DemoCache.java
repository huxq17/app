package com.aiqing.kaiheiba.neteasyim;

import android.content.Context;

import com.aiqing.kaiheiba.App;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

import user.UserService;

/**
 * Created by jezhee on 2/20/15.
 */
public class DemoCache {

    private static Context context;

    private static String account;

    private static StatusBarNotificationConfig notificationConfig;

    public static void clear() {
        account = null;
    }

    public static String getAccount() {

        return UserService.getAccount();
    }

    private static boolean mainTaskLaunching;

//    public static void setAccount(String account) {
//        UserService.s
//    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        DemoCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return App.getContext();
    }

//    public static void setContext(Context context) {
//        DemoCache.context = context.getApplicationContext();
//
//        AVChatKit.setContext(context);
//    }

//    public static void setMainTaskLaunching(boolean mainTaskLaunching) {
//        DemoCache.mainTaskLaunching = mainTaskLaunching;
//
//        AVChatKit.setMainTaskLaunching(mainTaskLaunching);
//    }

    public static boolean isMainTaskLaunching() {
        return mainTaskLaunching;
    }
}
