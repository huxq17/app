package com.aiqing.kaiheiba;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.aiqing.kaiheiba.neteasyim.session.SessionHelper;
import com.aiqing.kaiheiba.weex.WeexImageAdapter;
import com.aiqing.kaiheiba.weex.WeexJumpModule;
import com.aiqing.kaiheiba.weex.WeexShareModule;
import com.aiqing.kaiheiba.weex.WeexUploadModule;
import com.aiqing.kaiheiba.weex.WeexValueModule;
import com.alibaba.android.bindingx.plugin.weex.BindingX;
import com.huxq17.xprefs.XPrefs;
import com.imagepicker.ImagePickerModule;
import com.lljjcoder.style.citylist.utils.CityListLoader;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.taobao.weex.utils.LogLevel;

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
        try {
            BindingX.register();
        } catch (WXException e) {
            e.printStackTrace();
        }
        InitConfig initConfig = new InitConfig.Builder().setImgAdapter(new WeexImageAdapter()).build();
        WXSDKEngine.initialize(this, initConfig);
        WXEnvironment.sLogLevel = LogLevel.ERROR;
        try {
            WXSDKEngine.registerModule("environment", WeexValueModule.class);
            WXSDKEngine.registerModule("imagePicker", ImagePickerModule.class);
            WXSDKEngine.registerModule("profile", WeexJumpModule.class);
            WXSDKEngine.registerModule("imageUploader", WeexUploadModule.class);
            WXSDKEngine.registerModule("sharePost", WeexShareModule.class);
        } catch (WXException e) {
            e.printStackTrace();
        }
        CityListLoader.getInstance().loadCityData(this);
//        Config config = new Config();
//        //set database path.
////    config.setDatabaseName("/sdcard/a/d.db");
////      config.setDownloadDBController(dbController);
//
//        //set download quantity at the same time.
//        config.setDownloadThread(5);
//
//        //set each download info thread number
//        config.setEachDownloadThread(3);
//
//        // set connect timeout,unit millisecond
//        config.setConnectTimeout(10000);
//
//        // set read data timeout,unit millisecond
//        config.setReadTimeout(10000);
//        DownloadService.getDownloadManager(this.getApplicationContext(), config);
        SDKOptions options = new SDKOptions();
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, loginInfo(), options);

        // ... your codes
        if (NIMUtil.isMainProcess(this)) {
            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用
            initUiKit();
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
    }

    public static Context getContext() {
        return context;
    }
}
