package com.aiqing.kaiheiba.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.login.LoginAct;
import com.huxq17.xprefs.XPrefs;

import user.User;

public class SettingsAct extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("设置");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.settings_safe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToSafeSettings();
            }
        });
        findViewById(R.id.settings_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToFeedback();
            }
        });
        findViewById(R.id.settings_aboutus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsAct.this, AboutUsAct.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.settings_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               toast("清除成功");
            }
        });
    }

    public void logout(View v) {
        User user = new User();
        XPrefs.saveAll(user);
        LoginAct.start(this, true);
    }

    private void jumpToFeedback() {
        Intent intent = new Intent(SettingsAct.this, FeedbackAct.class);
        startActivity(intent);
    }

    private void jumpToSafeSettings() {
        Intent intent = new Intent(SettingsAct.this, SafeSettingsAct.class);
        startActivity(intent);
    }

}
