package com.aiqing.kaiheiba.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;

import java.util.HashMap;
import java.util.Map;

import user.User;


public class RegisterAct extends BaseActivity implements View.OnClickListener {
    private EditText etMobile, etPass, etMobleCode, etInviteCode;
    private CheckBox cbPassVisibility, cbGetMobileCode, cbProtocol;
    private CodeSender mCodeSender;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        mCodeSender = new CodeSender(cbGetMobileCode);
        mCodeSender.setFinishedListener(new CodeSender.FinishedListener() {

            @Override
            public void onFinished() {
                cbGetMobileCode.setChecked(true);
            }
        });
    }

    private void initView() {
        etMobile = findViewById(R.id.et_mobile);
        etPass = findViewById(R.id.et_pass);
        etMobleCode = findViewById(R.id.et_mobile_code);
        etInviteCode = findViewById(R.id.et_invite_code);
        cbPassVisibility = findViewById(R.id.cb_pass_visiblity);
        cbGetMobileCode = findViewById(R.id.cb_get_mobile_code);
        cbProtocol = findViewById(R.id.cb_protocol);
        cbPassVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etPass.setTransformationMethod(isChecked ? HideReturnsTransformationMethod.getInstance()
                        : PasswordTransformationMethod.getInstance());
                etPass.requestFocus();
                etPass.setSelection(getText(etPass).length());
            }
        });
        cbGetMobileCode.setOnClickListener(this);
        findViewById(R.id.tv_protocol).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProtocolAct.start(RegisterAct.this);
            }
        });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        switch (id) {
            case R.id.cb_get_mobile_code:
                requestMobileCode();
                break;
        }
    }

    public void register(View v) {
        if (!cbProtocol.isChecked()) {
            toast("注册需同意《17开黑吧服务条款》");
            return;
        }
        if (isEmpty(etMobile) || isEmpty(etPass) || isEmpty(etMobleCode)) {
            toast("请完善信息");
            if (isEmpty(etMobile)) {
                etMobile.requestFocus();
            } else if (isEmpty(etPass)) {
                etPass.requestFocus();
            } else if (isEmpty(etMobleCode)) {
                etMobleCode.requestFocus();
            }
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("mobile", getText(etMobile));
        params.put("password", getText(etPass));
        params.put("code", getText(etMobleCode));
        if (!isEmpty(etInviteCode)) {
            params.put("invite_code", getText(etInviteCode));
        }
        ApiManager.INSTANCE.getApi(LoginApi.class).register(params)
                .compose(RxSchedulers.<LoginApi.Bean>compose())
                .subscribe(new BaseObserver<User>() {
                    @Override
                    protected void onSuccess(User o) {
                        toast("注册成功");
                        finish();
                    }
                });
    }

    private void requestMobileCode() {
        if (isEmpty(etMobile)) {
            toast("请填写手机号");
            cbGetMobileCode.setChecked(true);
            return;
        }
        mCodeSender.codeSent();
        ApiManager.INSTANCE.getApi(LoginApi.class).obtainMobileCode(getText(etMobile), 2)
                .compose(RxSchedulers.<LoginApi.Bean>compose())
                .subscribe(new BaseObserver<User>() {
                    @Override
                    protected void onSuccess(User dataBean) {
                        toast("验证码已发送，请查收短信");
                        mCodeSender.startWait();
                    }

                    @Override
                    protected void onFailed(String msg) {
                        super.onFailed(msg);
                        mCodeSender.sendFailed();
                        cbGetMobileCode.setChecked(true);
                    }
                });
    }
}
