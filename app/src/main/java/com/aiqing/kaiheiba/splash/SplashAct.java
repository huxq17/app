package com.aiqing.kaiheiba.splash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aiqing.kaiheiba.HomeActivity;
import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class SplashAct extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private final int RC_BASIC_PERMISSIONS = 1;
    /**
     * 基本权限管理
     */
    private final String[] BASIC_PERMISSIONS = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    @AfterPermissionGranted(RC_BASIC_PERMISSIONS)
    private void onBasicPermissionGranted() {
//        takePhoto();

    }

    private boolean isStart = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isStart = true;
        if (!EasyPermissions.hasPermissions(this, BASIC_PERMISSIONS)) {
            EasyPermissions.requestPermissions(this, "程序运行需要一些权限", RC_BASIC_PERMISSIONS, BASIC_PERMISSIONS);
            return;
        }
        redirectToMainAct();
    }

    private void redirectToMainAct() {
        if (isStart) {
            isStart = false;
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    HomeActivity.start(SplashAct.this);
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (!EasyPermissions.hasPermissions(this, BASIC_PERMISSIONS)) {
            EasyPermissions.requestPermissions(this, "未全部授权，部分功能可能无法正常运行！", RC_BASIC_PERMISSIONS, BASIC_PERMISSIONS);
        }
        redirectToMainAct();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    }
}
