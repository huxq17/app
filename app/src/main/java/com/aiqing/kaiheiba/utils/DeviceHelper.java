/*
 * @Copyright (C) 2014 Guangyu Information Technology Co., Ltd.
 */
package com.aiqing.kaiheiba.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 获取设备信息的工具类,采用单例模式
 *
 * @author Tristan
 * @since 2014-11-17
 */
public class DeviceHelper {

    private Context context;
    private static DeviceHelper deviceHelper;

    private DeviceHelper(Context c) {
        this.context = c.getApplicationContext();
    }

    /**
     * get a DeviceHelper instance
     */
    public static DeviceHelper getInstance(Context context) {
        if (deviceHelper == null && context != null) {
            deviceHelper = new DeviceHelper(context);
        }
        return deviceHelper;
    }

    /**
     * checkPermissions
     *
     * @param permission
     * @return true or false
     */
    public boolean checkPermissions(String permission) {
        PackageManager localPackageManager = context.getPackageManager();
        return localPackageManager.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Determine the current networking is WIFI
     *
     * @return true of false
     */
    public boolean currentNetworkTypeIsWIFI() {
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectionManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Judge wifi is available
     *
     * @return boolean
     */
    public boolean isWiFiActive() {
        if (checkPermissions("android.permission.ACCESS_WIFI_STATE")) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * Testing equipment networking and networking WIFI
     *
     * @return true or false
     */
    public boolean isNetworkAvailable() {
        if (checkPermissions("android.permission.INTERNET")) {
            ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Get the current time format yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    public String getTime() {
        Date date = new Date();
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return localSimpleDateFormat.format(date);
    }

    /**
     * long type of milliseconds time format yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    public String getTime(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return localSimpleDateFormat.format(date);
    }

    /**
     * get APPKEY
     *
     * @return appkey
     */
    public String getAppKey() {
        return getMetaDataValue("GUANGYUSDK_APPKEY");
    }

    /**
     * get CHANNEL VALUE
     *
     * @return appkey
     */
    public String getChannel() {
        return getMetaDataValue("GUANGYUSDK_CHANNEL");
    }


    //微信支付appid
    public String getWXAPPID() {
        return getMetaDataValue("WXPAY_APPID");
    }

    /**
     * MD5 encrypt
     */
    public String md5(String str) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(str.getBytes());
            byte[] arrayOfByte = localMessageDigest.digest();
            StringBuffer localStringBuffer = new StringBuffer();
            for (int i = 0; i < arrayOfByte.length; i++) {
                int j = 0xFF & arrayOfByte[i];
                if (j < 16)
                    localStringBuffer.append("0");
                localStringBuffer.append(Integer.toHexString(j));
            }
            return localStringBuffer.toString();
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
            localNoSuchAlgorithmException.printStackTrace();
        }
        return "";
    }

    /**
     * get MetaData VALUE
     *
     * @return appkey
     */
    public String getMetaDataValue(String name) {
        String appkey;
        try {
            PackageManager localPackageManager = context.getPackageManager();
            ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(context.getPackageName(), 128);
            if (localApplicationInfo != null) {
                String str = localApplicationInfo.metaData.getString(name);
                if (str != null) {
                    appkey = str;
                    return appkey.toString();
                }
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return "";
    }

    /**
     * get currnet activity's name
     */
    public String getActivityName() {
        if (context == null) {
            return "";
        }
        // String activityName = context.getClass().getName();
        // if(!activityName.contains("android.app.Application")){
        // return activityName;
        // }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (checkPermissions("android.permission.GET_TASKS")) {
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getClassName();
        } else {
            return "";
        }

    }

    /**
     * get PackageName
     */
    public String getPackageName() {
        return context.getPackageName();
    }

    /**
     * get OS number
     *
     * @return the type of 4.4.2
     */
    public static String getSysVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Get the version number of the current program
     *
     * @return String
     */

    public String getCurVersion() {
        String curversion = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            curversion = pi.versionName;
            if (curversion == null || curversion.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
        }
        return curversion;
    }

    /**
     * To determine whether it contains a gyroscope
     *
     * @return boolean
     */
    public boolean isHaveGravity() {
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (manager == null) {
            return false;
        }
        return true;
    }

    /**
     * Determine the current network type
     *
     * @return boolean
     */
    public boolean isNetworkTypeWifi() {
        // TODO Auto-generated method stub

        if (checkPermissions("android.permission.INTERNET")) {
            ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cManager.getActiveNetworkInfo();

            if (info != null && info.isAvailable() && info.getTypeName().equals("WIFI")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    /**
     * Get the current application version name
     *
     * @return String
     */
    public String getVersionName() {
        String versionName = "";
        try {
            if (context == null) {
                return "";
            }
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {

        }
        return versionName;
    }

    /**
     * Get the current application version code
     *
     * @return String
     */
    public String getVersionCode() {
        try {
            if (context == null) {
                return "";
            }
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(pi.versionCode);
        } catch (Exception e) {
        }
        return "0";
    }

    /**
     * get the carrier number
     */
    public String getCarrier() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "-1";
        }

        String operator = tm.getSimOperator();
        if (TextUtils.isEmpty(operator)) {
            operator = "-1";
        }
        return operator;
    }

    public String getNetworkType() {
        String strNetworkType = "";

        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }
                        break;
                }
            }
        }
        return strNetworkType;
    }

    /**
     * device model
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * device factory
     */
    public String getFactory() {
        return Build.MANUFACTURER;
    }

    /**
     * device manu time
     */
    public String getManuTime() {
        return String.valueOf(Build.TIME);
    }

    /**
     * device system build version
     */
    public String getManuID() {
        return Build.ID;
    }

    /**
     * device language
     */
    public String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * device system time zone
     */
    public String getTimeZone() {
        return TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
    }

    /**
     * device is rooted or not
     */
    public boolean isRooted() {
        int kSystemRootStateUnknow = -1;
        int kSystemRootStateDisable = 0;
        int kSystemRootStateEnable = 1;
        int systemRootState = kSystemRootStateUnknow;

        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {
            return false;
        }
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }

    /**
     * get CPU name
     */
    public String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            @SuppressWarnings("resource")
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String byteToHex(byte[] data) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            buffer.append(String.format("%02x", data[i]));
        }
        return buffer.toString();
    }

    private byte[] SHA1(String text) throws Throwable {
        byte[] data = text.getBytes("utf-8");
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(data);
        return md.digest();
    }

    /**
     * Judge mount sdcard or not
     */
    private boolean getSdcardState() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * get device key from the local file
     */
    private String getLocalDeviceKey() throws Throwable {
        /*if (!getSdcardState()) {
            return null;
		}

		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		File cacheRoot = new File(sdPath, "GUANGYUBI");
		if (!cacheRoot.exists()) {
			return null;
		}*/

        File keyFile = new File("/data/data/" + context.getPackageName() + "/key.dk");
        if (!keyFile.exists()) {
            return null;
        }

        FileInputStream fis = new FileInputStream(keyFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object key = ois.readObject();
        String strKey = null;
        if (key != null && key instanceof char[]) {
            char[] cKey = (char[]) key;
            strKey = String.valueOf(cKey);
        }
        ois.close();
        return strKey;
    }

    private void saveLocalDeviceKey(String key) throws Throwable {
        /*if (!getSdcardState()) {
            return;
		}
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		File cacheRoot = new File(sdPath, "GUANGYUBI");
		if (!cacheRoot.exists()) {
			cacheRoot.mkdirs();
		}*/
        File keyFile = new File("/data/data/" + context.getPackageName() + "/key.dk");
        if (keyFile.exists()) {
            keyFile.delete();
        }

        FileOutputStream fos = new FileOutputStream(keyFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        char[] cKey = key.toCharArray();
        oos.writeObject(cKey);
        oos.flush();
        oos.close();
    }

    public String getMacAddress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return MacUtils.getMac();
        }
        @SuppressLint("WifiManagerLeak") WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return "";
        }

        String mac = null;
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            mac = info.getMacAddress();
        }
        return (mac == null) ? "" : mac;
    }

    private Object getSystemService(String name) {
        try {
            return context.getSystemService(name);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    private Location getLocation() {
        if (!checkPermissions("android.permission.ACCESS_FINE_LOCATION") || !checkPermissions("android.permission.ACCESS_COARSE_LOCATION")) {
            return null;
        }
        LocationManager loctionManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> matchingProviders = loctionManager.getAllProviders();
        Location location = null;
        for (String prociderString : matchingProviders) {
            location = loctionManager.getLastKnownLocation(prociderString);
        }
        return location;
    }


    /**
     * Getting latitude of location
     *
     * @return
     */
    public String getLatitude() {
        String latitude = "";
        Location location = getLocation();
        if (location != null) {
            latitude = String.valueOf(location.getLatitude());
        }
        return latitude;
    }

    /**
     * Getting longtitude of location
     *
     * @return
     */
    public String getLongitude() {
        String longtitude = "";
        Location location = deviceHelper.getLocation();
        if (location != null) {
            longtitude = String.valueOf(location.getLongitude());
        }
        return longtitude;
    }
}
