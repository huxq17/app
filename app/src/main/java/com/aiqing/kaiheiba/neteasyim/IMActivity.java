package com.aiqing.kaiheiba.neteasyim;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.aiqing.kaiheiba.R;
import com.netease.nim.uikit.common.activity.UI;

public class IMActivity extends UI {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im);
        getSupportFragmentManager()
                .beginTransaction().replace(R.id.frg_im_container, new IMFragment())
                .commit();
    }
}
