package com.aiqing.kaiheiba;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.aiqing.kaiheiba.neteasyim.DemoCache;
import com.aiqing.kaiheiba.neteasyim.mixpush.DemoPushContentProvider;
import com.aiqing.kaiheiba.neteasyim.session.SessionHelper;
import com.aiqing.kaiheiba.weex.ImagePickerModule;
import com.aiqing.kaiheiba.weex.WeexImageAdapter;
import com.aiqing.kaiheiba.weex.WeexJumpModule;
import com.aiqing.kaiheiba.weex.WeexShareModule;
import com.aiqing.kaiheiba.weex.WeexUploadModule;
import com.aiqing.kaiheiba.weex.WeexValueModule;
import com.aiqing.kaiheiba.weex.WeexWindowSizeModule;
import com.aiyou.sdk.LGSDK;
import com.alibaba.android.bindingx.plugin.weex.BindingX;
import com.huxq17.xprefs.XPrefs;
import com.lljjcoder.style.citylist.utils.CityListLoader;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.taobao.weex.utils.LogLevel;

import java.io.IOException;

import user.UserService;

public class App extends MultiDexApplication {
    private static Context context;
    private static Activity curAct;

    public static Activity getCurAct() {
        return curAct;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        context = this;
        XPrefs.bind(this);
        InitConfig initConfig = new InitConfig.Builder().setImgAdapter(new WeexImageAdapter()).build();
        WXSDKEngine.initialize(this, initConfig);
        WXEnvironment.sLogLevel = LogLevel.ERROR;
        CityListLoader.getInstance().loadCityData(this);

        SDKOptions options = new SDKOptions();
        options.sdkStorageRootPath = getAppCacheDir(this)+"/im";
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = HomeActivity.class;// 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.mipmap.iv_app_icon;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
//        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, loginInfo(), options);
        LGSDK.init(this, "211534", false);
        // ... your codes
        if (NIMUtil.isMainProcess(this)) {
            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用
            initUiKit();
            try {
                WXSDKEngine.registerModule("environment", WeexValueModule.class);
                WXSDKEngine.registerModule("imagePicker", ImagePickerModule.class);
                WXSDKEngine.registerModule("profile", WeexJumpModule.class);
                WXSDKEngine.registerModule("imageUploader", WeexUploadModule.class);
                WXSDKEngine.registerModule("sharePost", WeexShareModule.class);
                WXSDKEngine.registerModule("windowSize", WeexWindowSizeModule.class);
            } catch (WXException e) {
                e.printStackTrace();
            }
            try {
                BindingX.register();
            } catch (WXException e) {
                e.printStackTrace();
            }
        }
    }


    private LoginInfo loginInfo() {
        String account = UserService.getAccount();
        String token = UserService.getIMToken();
        String appKey = UserService.getAppKey();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            NimUIKit.setAccount(account);
            return new LoginInfo(account, token, appKey);
        } else {
            return null;
        }
    }

    private void initUiKit() {
        NimUIKit.init(this);
        SessionHelper.init();

        // 添加自定义推送文案以及选项，请开发者在各端（Android、IOS、PC、Web）消息发送时保持一致，以免出现通知不一致的情况
        NimUIKit.setCustomPushContentProvider(new DemoPushContentProvider());
    }

    public static Context getContext() {
        return context;
    }
    /**
     * 配置 APP 保存图片/语音/文件/log等数据的目录
     * 这里示例用SD卡的应用扩展存储目录
     */
    static String getAppCacheDir(Context context) {
        String storageRootPath = null;
        try {
            // SD卡应用扩展存储区(APP卸载后，该目录下被清除，用户也可以在设置界面中手动清除)，请根据APP对数据缓存的重要性及生命周期来决定是否采用此缓存目录.
            // 该存储区在API 19以上不需要写权限，即可配置 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="18"/>
            if (context.getExternalCacheDir() != null) {
                storageRootPath = context.getExternalCacheDir().getCanonicalPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(storageRootPath)) {
            // SD卡应用公共存储区(APP卸载后，该目录不会被清除，下载安装APP后，缓存数据依然可以被加载。SDK默认使用此目录)，该存储区域需要写权限!
            storageRootPath = Environment.getExternalStorageDirectory() + "/" + DemoCache.getContext().getPackageName();
        }

        return storageRootPath;
    }
}
