package com.aiqing.kaiheiba.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.widget.IWebView;

public class ProtocolAct extends BaseActivity{
    IWebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        setTitle("服务条款");
         webView = findViewById(R.id.wv_protocol);
        webView.loadUrl("http://weex.17kaiheiba.com/active/service.html");

    }
    public static void start(Activity context){
        Intent intent = new Intent(context,ProtocolAct.class);
        context.startActivity(intent);
    }
}
