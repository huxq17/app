package com.aiqing.kaiheiba;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.aiqing.kaiheiba.common.BaseFragment;
import com.aiqing.kaiheiba.login.LoginAct;
import com.aiqing.kaiheiba.neteasyim.IMFragment;
import com.aiqing.kaiheiba.personal.download.MyBusinessInfLocal;
import com.aiqing.kaiheiba.personal.download.MyDownloadAct;
import com.aiqing.kaiheiba.weex.WeexFragment;
import com.netease.nim.uikit.common.activity.UI;
import com.taobao.weex.WXEnvironment;

import java.util.ArrayList;
import java.util.List;

import user.UserService;


public class HomeActivity extends UI {
    private LinearLayout mBottomBar;
    private FragmentTransaction transaction;
    private WeexFragment homeFragment;
    private WeexFragment gameFragment;
    private BaseFragment imFragment;
    private BaseFragment myFragment;
    List<Fragment> fragmentList = new ArrayList<>();
    RadioButton rbGame, rbPlayGround, rbIM, rbMy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    private int lastSelectedId;

    private void initView() {
        mBottomBar = findViewById(R.id.tab_bottom_home);
//        mBottomBar.noTabletGoodness();
//        mBottomBar.useFixedMode();
        rbGame = findView(R.id.rb_home_game);
        rbPlayGround = findView(R.id.rb_home_playground);
        rbIM = findView(R.id.rb_home_im);
        rbMy = findView(R.id.rb_home_my);
        toggle(R.id.rb_home_playground);
    }

    public void toggle(int id) {
        int count = mBottomBar.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton button = (RadioButton) mBottomBar.getChildAt(i);
            if (id == button.getId()) {
                button.performClick();
            } else {
                button.setChecked(false);
            }
        }
    }

    public void closeOther(int id) {
        int count = mBottomBar.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton button = (RadioButton) mBottomBar.getChildAt(i);
            if (id != button.getId()) {
                button.setChecked(false);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存BottomBar的状态
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        transaction.replace(R.id.main_fragment_layout, fragment);
//        transaction.hide();
        transaction.commit();
    }

    private void defaultFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        transaction.add(R.id.main_fragment_layout, fragment);
        transaction.commit();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void onCheck(View v) {
        int checkedId = v.getId();
        switch (checkedId) {
            case R.id.rb_home_game:
                //                    if (homeFragment == null) {
//                        homeFragment = WeexFragment.newInstance(WeexFragment.gameurl);
//                        fragmentList.add(homeFragment);
//                    }
//                    replaceFragment(homeFragment);
                String url = "https://t.alipayobjects.com/L1/71/100/and/alipay_wap_main.apk";
                MyBusinessInfLocal myBusinessInfLocal = new MyBusinessInfLocal(
                        url.hashCode(), "", "", url);
                MyDownloadAct.start(WXEnvironment.getApplication(), myBusinessInfLocal);
                break;
            case R.id.rb_home_playground:
                if (gameFragment == null) {
                    gameFragment = WeexFragment.newInstance(WeexFragment.homeurl);
                    fragmentList.add(gameFragment);
                }
                replaceFragment(gameFragment);
                break;
            case R.id.rb_home_im:
                if (!UserService.isLogin()) {
                    LoginAct.start(HomeActivity.this);
//                    toggle(lastSelectedId);
                    rbIM.setChecked(false);
                    return;
                }
                if (imFragment == null) {
                    imFragment = IMFragment.newInstance();
                    fragmentList.add(imFragment);
                }
                replaceFragment(imFragment);
                break;
            case R.id.rb_home_my:
                if (!UserService.isLogin()) {
                    LoginAct.start(HomeActivity.this);
//                    toggle(lastSelectedId);
                    rbMy.setChecked(false);
                    return;
                }
                if (myFragment == null) {
                    myFragment = WeexFragment.newInstance(WeexFragment.mypageurl);
                    fragmentList.add(myFragment);
                }
                replaceFragment(myFragment);
                break;
        }
        lastSelectedId = checkedId;
        closeOther(checkedId);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        moveTaskToBack(true);
    }
}
