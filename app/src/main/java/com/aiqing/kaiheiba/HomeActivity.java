package com.aiqing.kaiheiba;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.aiqing.kaiheiba.common.BaseFragment;
import com.aiqing.kaiheiba.login.LoginAct;
import com.aiqing.kaiheiba.neteasyim.IMFragment;
import com.aiqing.kaiheiba.neteasyim.UserStatusObserver;
import com.aiqing.kaiheiba.utils.Apk;
import com.aiqing.kaiheiba.utils.FragmentController;
import com.aiqing.kaiheiba.utils.VersionUpgrade;
import com.aiqing.kaiheiba.weex.WeexFragment;
import com.aiqing.kaiheiba.weex.WeexWindowSizeModule;
import com.huxq17.xprefs.XPrefs;
import com.netease.nim.uikit.common.activity.UI;

import user.UserService;


public class HomeActivity extends UI {
    private LinearLayout mBottomBar;
    private WeexFragment homeFragment;
    private WeexFragment gameFragment;
    private BaseFragment imFragment;
    private WeexFragment myFragment;
    RadioButton rbGame, rbPlayGround, rbIM, rbMy;
    private View conentView;
    private boolean hasRegister;
    UserStatusObserver userStatusObserver;
    private static final String CUR_TAB_ID = "CURRENT_TAB_ID";
    private int curTabId;
    FragmentController<BaseFragment> fragmentController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        fragmentController = new FragmentController<>(getSupportFragmentManager());
        userStatusObserver = new UserStatusObserver(this);
        new VersionUpgrade(this).check();
        if (savedInstanceState != null) {
            curTabId = savedInstanceState.getInt(CUR_TAB_ID);
        } else {
            curTabId = R.id.rb_home_playground;
        }
        findViewById(curTabId).performClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rbMy.isChecked()) {
            if (myFragment != null) {
                myFragment.send();
            }
        }
        if (!hasRegister && UserService.isLogin()) {
            hasRegister = true;
            userStatusObserver.register();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hasRegister) {
            userStatusObserver.unregister();
        }
    }

    private void initView() {
        mBottomBar = findViewById(R.id.tab_bottom_home);
        conentView = findViewById(R.id.main_fragment_layout);
        rbGame = findView(R.id.rb_home_game);
        rbPlayGround = findView(R.id.rb_home_playground);
        rbIM = findView(R.id.rb_home_im);
        rbMy = findView(R.id.rb_home_my);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WeexWindowSizeModule.WindowSize windowSize = new WeexWindowSizeModule.WindowSize(conentView.getWidth(), conentView.getHeight());
            XPrefs.saveAll(windowSize);
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
        outState.putInt(CUR_TAB_ID, curTabId);
    }

    private void replaceFragment(Fragment fragment) {
        fragmentController.show(fragment, R.id.main_fragment_layout);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void onCheck(View v) {
        int checkedId = v.getId();
        if (curTabId == 0 && curTabId == checkedId) return;
        switch (checkedId) {
            case R.id.rb_home_game:
                if (homeFragment == null) {
                    homeFragment = getWeexFragmentById(WeexFragment.gameurl, R.id.rb_home_game);
                }
                replaceFragment(homeFragment);
                break;
            case R.id.rb_home_playground:
                if (gameFragment == null) {
                    gameFragment = getWeexFragmentById(WeexFragment.homeurl, R.id.rb_home_playground);
                }
                replaceFragment(gameFragment);
                break;
            case R.id.rb_home_im:
                if (!UserService.isLogin()) {
                    LoginAct.start(HomeActivity.this);
                    rbIM.setChecked(false);
                    return;
                }
                if (imFragment == null) {
                    imFragment = getIMFragmentById();
                }
                replaceFragment(imFragment);
                break;
            case R.id.rb_home_my:
                if (myFragment == null) {
                    myFragment = getWeexFragmentById(WeexFragment.mypageurl, R.id.rb_home_my);
                }
                myFragment.send();
                replaceFragment(myFragment);
                break;
        }
        closeOther(checkedId);
        curTabId = checkedId;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Apk.INSTANCE.onActivityResult(requestCode, resultCode, data);
    }

    private BaseFragment getIMFragmentById() {
        int id = R.id.rb_home_im;
        Class<IMFragment> clazz = IMFragment.class;
        IMFragment fragment = fragmentController.getFragmentById(id, clazz, new FragmentController.Supplier<IMFragment>() {
            @Override
            public IMFragment get() {
                return IMFragment.newInstance();
            }
        });
        return fragment;
    }

    private WeexFragment getWeexFragmentById(final String url, int id) {
        Class<WeexFragment> clazz = WeexFragment.class;
        WeexFragment fragment = fragmentController.getFragmentById(id, clazz, new FragmentController.Supplier<WeexFragment>() {
            @Override
            public WeexFragment get() {
                return WeexFragment.newInstance(url);
            }
        });
        return fragment;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        moveTaskToBack(true);
    }
}
