package com.aiqing.kaiheiba.settings;

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
import com.aiqing.kaiheiba.login.LoginAct;
import com.aiqing.kaiheiba.login.LoginApi;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;

import java.util.HashMap;
import java.util.Map;

import user.User;
import user.UserService;

public class UpdatePassAct extends BaseActivity {
    private EditText etCurPass, etNewPass, etEnsurePass;
    private CheckBox cbCurPass, cbNewPass, cbEnsurePass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("修改密码");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass);
        etCurPass = findViewById(R.id.up_input_cur_pass);
        etNewPass = findViewById(R.id.up_input_new_pass);
        etEnsurePass = findViewById(R.id.up_input_check_pass);
        cbCurPass = findViewById(R.id.up_curpass_visiblity);
        cbNewPass = findViewById(R.id.up_new_pass_visiblity);
        cbEnsurePass = findViewById(R.id.up_check_pass_visiblity);
        cbCurPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etCurPass.setTransformationMethod(isChecked ? HideReturnsTransformationMethod.getInstance()
                        : PasswordTransformationMethod.getInstance());
                etCurPass.requestFocus();
                etCurPass.setSelection(getText(etCurPass).length());
            }
        });
        cbNewPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etNewPass.setTransformationMethod(isChecked ? HideReturnsTransformationMethod.getInstance()
                        : PasswordTransformationMethod.getInstance());
                etNewPass.requestFocus();
                etNewPass.setSelection(getText(etNewPass).length());
            }
        });
        cbEnsurePass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etEnsurePass.setTransformationMethod(isChecked ? HideReturnsTransformationMethod.getInstance()
                        : PasswordTransformationMethod.getInstance());
                etEnsurePass.requestFocus();
                etEnsurePass.setSelection(getText(etEnsurePass).length());
            }
        });
    }

    public void updatePass(View v) {
        if (isEmpty(etCurPass) || isEmpty(etNewPass) || isEmpty(etEnsurePass)) {
            toast("请完善信息");
            if (isEmpty(etCurPass)) {
                etCurPass.requestFocus();
            } else if (isEmpty(etNewPass)) {
                etNewPass.requestFocus();
            } else if (isEmpty(etEnsurePass)) {
                etEnsurePass.requestFocus();
            }
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("old_password", getText(etCurPass));
        params.put("new_password", getText(etNewPass));
        params.put("new_password2", getText(etEnsurePass));
        ApiManager.INSTANCE.getApi(LoginApi.class)
                .updatePass(params)
                .compose(RxSchedulers.<LoginApi.Bean>compose())
                .subscribe(new BaseObserver<User>(this,"正在修改密码...") {
                    @Override
                    protected void onSuccess(User dataBean) {
                        toast("修改密码成功");
                        User user = new User();
                        user.mobile = UserService.getMobile();
                        UserService.save(user);
                        LoginAct.start(UpdatePassAct.this, true);
                        finish();
                    }
                });
    }

}
