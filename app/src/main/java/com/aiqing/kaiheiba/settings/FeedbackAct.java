package com.aiqing.kaiheiba.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.FeedbackApi;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.bean.SimpleBean;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;

public class FeedbackAct extends BaseActivity {
    EditText etContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("建议与反馈");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        etContent = findViewById(R.id.feedback_input);
        findViewById(R.id.feedback_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(etContent)) {
                    toast("请填写建议与反馈");
                    return;
                }
                submit(getText(etContent));
            }
        });
    }

    private void submit(String content) {
        ApiManager.INSTANCE.getApi(FeedbackApi.class).feedback(content)
                .compose(RxSchedulers.<SimpleBean>compose())
                .subscribe(new BaseObserver<Object>(this,"正在提交...") {
                    @Override
                    protected void onSuccess(Object o) {
                        toast("提交成功");
                        finish();
                    }

                    @Override
                    protected void onFailed(String msg) {
                        super.onFailed(msg);
                        toast(msg);
                    }
                });
    }

}
