/*
 *
 *@Copyright (C) 2012 Menue Information Technology Co., Ltd.
 *
 */
package com.aiqing.kaiheiba.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 检查权限
 * @date 2012-10-16
 */

public class CheckPermission {
	public static boolean checkPermission(Context context, String permName) {
		PackageManager packageManager = context.getPackageManager();
		boolean havePermission = packageManager.checkPermission(permName,
				context.getPackageName()) == 0;
		return havePermission;
	}
	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
	private static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";

	public static boolean hasExternalStoragePermission(Context context) {
		int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		return perm == PackageManager.PERMISSION_GRANTED;
	}

	public static boolean hasReadPhoneStatePermission(Context context) {
		int perm = context.checkCallingOrSelfPermission(READ_PHONE_STATE);
		return perm == PackageManager.PERMISSION_GRANTED;
	}
}
