package com.aiqing.kaiheiba.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Device {
    private static String ID;

    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        if (!TextUtils.isEmpty(ID)) {
            return ID;
        }
        //读取本地存储的设备号
        String deviceId = SdCardUtils.readId(context.getApplicationContext());
        //判断是否为合法的设备号
        if (!isLegalDeviceId(deviceId)) {
            //判断是否有权限
            if (CheckPermission.hasReadPhoneStatePermission(context)) {
                try {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    if (tm != null) {
                        deviceId = tm.getDeviceId();
                    }
                } catch (Exception e) {
                    //ignore
                }
            }
            if (!isLegalDeviceId(deviceId)) {//获取android_id
                deviceId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                while (!isLegalDeviceId(deviceId)) {
                    deviceId = UUID.randomUUID().toString();
                }
            }
            //存储获取的设备号
            SdCardUtils.writeId(context.getApplicationContext(), deviceId);
        }
        return deviceId;
    }

    private static boolean isLegalDeviceId(String deviceId) {
        if (TextUtils.isEmpty(deviceId) || deviceId.equals("9774d56d682e549c") || isSameChar(deviceId) || deviceId.startsWith("[B@")
                || deviceId.equals("012345678912345")) {
            return false;
        }
        return true;
    }

    private static boolean isSameChar(String str) {
        Pattern pattern = Pattern.compile("^(.)\\1*$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
