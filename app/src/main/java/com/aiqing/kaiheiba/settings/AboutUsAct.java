package com.aiqing.kaiheiba.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CheckBox;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;

public class AboutUsAct extends BaseActivity {
    CheckBox bindButton;
    boolean isBind = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("关于我们");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
//        findViewById(R.id.settings_safe).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                jumpToSafeSettings();
//            }
//        });
    }

}
