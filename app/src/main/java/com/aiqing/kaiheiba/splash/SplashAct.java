package com.aiqing.kaiheiba.splash;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.aiqing.kaiheiba.HomeActivity;
import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;

import pub.devrel.easypermissions.EasyPermissions;


public class SplashAct extends BaseActivity {
    private final int RC_BASIC_PERMISSIONS = 1;
    /**
     * 基本权限管理
     */
    private final String[] BASIC_PERMISSIONS = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECORD_AUDIO
//            ,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    private boolean isStart = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isStart = true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EasyPermissions.hasPermissions(this, BASIC_PERMISSIONS)) {
//            EasyPermissions.requestPermissions(this, "程序运行需要一些权限", RC_BASIC_PERMISSIONS, BASIC_PERMISSIONS);
            ActivityCompat.requestPermissions(this, BASIC_PERMISSIONS, RC_BASIC_PERMISSIONS);
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

    public boolean neverAsk(String[] perms) {
        boolean neverAsk = false;
        for (String perm : perms) {
            if (!EasyPermissions.hasPermissions(this, perm)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                neverAsk = true;
                break;
            }
        }
        return neverAsk;
    }

    public boolean checkPermission(String[] perms) {
        boolean permissionsGrated = true;
        for (String perm : perms) {
            if (ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                permissionsGrated = false;
                break;
            }
        }
        return permissionsGrated;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionsGranted = EasyPermissions.hasPermissions(this, permissions);
//
//        for (int i = 0; i < permissions.length; i++) {
//            final boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
//            permissionsGranted = permissionsGranted && granted;
//        }
        if (!permissionsGranted) {
            final Boolean dontAskAgain = neverAsk(permissions);
            if (dontAskAgain) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, com.aiqing.imagepicker.R.style.DefaultExplainingPermissionsTheme);
                builder
                        .setTitle("缺少权限")
                        .setMessage("未全部授权，部分功能可能无法正常运行！")
                        .setCancelable(false)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface,
                                                int i) {
                                redirectToMainAct();
                            }
                        })
                        .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", SplashAct.this.getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
//                                redirectToMainAct();
                            }
                        });
                builder.create().show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, com.aiqing.imagepicker.R.style.DefaultExplainingPermissionsTheme);
                builder
                        .setTitle("是否授予权限")
                        .setMessage("程序运行需要权限，否则部分功能可能无法正常运行！")
                        .setCancelable(false)
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface,
                                                int i) {
                                redirectToMainAct();
                            }
                        })
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                ActivityCompat.requestPermissions(SplashAct.this, BASIC_PERMISSIONS, RC_BASIC_PERMISSIONS);
                            }
                        });
                builder.create().show();
            }
            return;
        }
        redirectToMainAct();
    }
}