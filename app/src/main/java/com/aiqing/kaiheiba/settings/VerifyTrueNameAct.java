package com.aiqing.kaiheiba.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.UserApi;
import com.aiqing.kaiheiba.bean.BaseResponse;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;

import user.UserService;

public class VerifyTrueNameAct extends BaseActivity {
    EditText etName, etID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("实名认证");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_truename);
        etName = findViewById(R.id.verify_truename_input);
        etID = findViewById(R.id.verify_idcard_tag_input);
    }

    public void submit(View v) {
        if(isEmpty(etName)||isEmpty(etID)){
            toast("请完善信息");
            return;
        }
        ApiManager.INSTANCE.getApi(UserApi.class)
                .verifyTrueName(UserService.getUserToken(),getText(etName),getText(etID))
                .compose(RxSchedulers.<BaseResponse<Object>>compose())
                .subscribe(new BaseObserver<Object>() {
                    @Override
                    protected void onSuccess(Object o) {
                        toast("验证成功");
                        finish();
                    }
                });
    }

}
