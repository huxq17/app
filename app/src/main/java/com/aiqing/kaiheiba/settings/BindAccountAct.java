package com.aiqing.kaiheiba.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;

public class BindAccountAct extends BaseActivity {
    CheckBox bindButton;
    boolean isBind = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("帐号绑定");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);
//        findViewById(R.id.settings_safe).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                jumpToSafeSettings();
//            }
//        });
        bindButton = findViewById(R.id.bind_button);
        changeBindStatus();
        bindButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isBind = !isBind;
                changeBindStatus();
            }
        });
    }

    private void changeBindStatus() {
        if (isBind) {
            bindButton.setText("已绑定");
        } else {
            bindButton.setText("点击绑定");
        }
    }

    private void jumpToSafeSettings() {
        Intent intent = new Intent(BindAccountAct.this, SafeSettingsAct.class);
        startActivity(intent);
    }

}
