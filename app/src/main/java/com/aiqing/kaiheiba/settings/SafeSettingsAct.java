package com.aiqing.kaiheiba.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;

public class SafeSettingsAct extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("安全设置");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_settings);
        findViewById(R.id.settings_safe_update_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToUpdatePass();
            }
        });
        findViewById(R.id.settings_safe_verify_truename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToVerifyTrueName();
            }
        });
        findViewById(R.id.settings_safe_bind_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToBindAccount();
            }
        });
    }

    private void jumpToUpdatePass() {
        Intent intent = new Intent(this, UpdatePassAct.class);
        startActivity(intent);
    }
    private void jumpToVerifyTrueName() {
        Intent intent = new Intent(this, VerifyTrueNameAct.class);
        startActivity(intent);
    }
    private void jumpToBindAccount() {
        Intent intent = new Intent(this, BindAccountAct.class);
        startActivity(intent);
    }
}
