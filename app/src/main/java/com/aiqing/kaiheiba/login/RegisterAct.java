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
import com.aiqing.kaiheiba.api.Code;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import user.User;
import user.UserService;


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
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<LoginApi.Bean, ObservableSource<LoginApi.Bean>>() {
                    @Override
                    public ObservableSource<LoginApi.Bean> apply(LoginApi.Bean bean) throws Exception {
                        return loginNetEasyIM(bean);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<User>() {
                    @Override
                    protected void onSuccess(User user) {
                        toast("注册成功");
                        user.mobile = getText(etMobile);
                        UserService.save(user);
                        finish();
                        UpdateProfileAct.start(RegisterAct.this);
                    }
                });
//                .compose(RxSchedulers.<LoginApi.Bean>compose())
//                .subscribe(new BaseObserver<User>() {
//                    @Override
//                    protected void onSuccess(User o) {
//                        toast("注册成功");
//                        finish();
//                    }
//                });
    }

    private Observable<LoginApi.Bean> loginNetEasyIM(final LoginApi.Bean bean) {
        return Observable.create(new ObservableOnSubscribe<LoginApi.Bean>() {
            @Override
            public void subscribe(final ObservableEmitter<LoginApi.Bean> e) throws Exception {
                if (bean == null) {
                    e.onNext(bean);
                    return;
                }
                int code = bean.getCode();
                if (code == Code.OK) {
                    final User user = bean.getData();
                    String account = user.accid;
                    final String token = user.imToken;
                    NimUIKit.login(new LoginInfo(account, token), new RequestCallback<LoginInfo>() {
                        @Override
                        public void onSuccess(LoginInfo param) {
                            bean.getData().appKey = param.getAppKey();
                            NimUIKit.loginSuccess(user.accid);
                            e.onNext(bean);
                        }

                        @Override
                        public void onFailed(int code) {
                            if (code == 302 || code == 404) {
                                e.onError(new Exception("帐号或密码错误"));
                            } else {
                                e.onError(new Exception("登录失败: " + code));
                            }
                        }

                        @Override
                        public void onException(Throwable exception) {
                            e.onError(exception);
                        }
                    });
                } else {
                    e.onNext(bean);
                }

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
