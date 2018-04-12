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

import com.aiqing.kaiheiba.HomeActivity;
import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.Code;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.neteasyim.LogoutHelper;
import com.aiqing.kaiheiba.neteasyim.Preferences;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
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


public class LoginAct extends BaseActivity {
    private EditText etMobile, etPass;
    private CheckBox cbPassVisibility;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dealClick();
        etMobile = findViewById(R.id.et_mobile);
        etPass = findViewById(R.id.et_pass);
        cbPassVisibility = findViewById(R.id.cb_pass_visiblity);
        cbPassVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etPass.setTransformationMethod(isChecked ? HideReturnsTransformationMethod.getInstance()
                        : PasswordTransformationMethod.getInstance());
                etPass.requestFocus();
                etPass.setSelection(getText(etPass).length());
            }
        });
        inflateMobile();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        inflateMobile();
    }

    private void inflateMobile() {
        String mobile = UserService.getMobile();
        if (!isEmpty(mobile)) {
            etMobile.setText(mobile);
            etMobile.setSelection(mobile.length());
            etPass.requestFocus();
        }
    }

    private void dealClick() {
        findViewById(R.id.tv_forget_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPassAct.start(LoginAct.this);
            }
        });
        findViewById(R.id.tv_go_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterAct.start(LoginAct.this);
            }
        });
    }

    public void login(View v) {
        if (isEmpty(etMobile) || isEmpty(etPass)) {
            toast("请完善信息");
            if (isEmpty(etMobile)) {
                etMobile.requestFocus();
            } else if (isEmpty(etPass)) {
                etPass.requestFocus();
            }
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", getText(etMobile));
        params.put("password", getText(etPass));
        ApiManager.INSTANCE
                .getApi(LoginApi.class).login(params)
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
                        toast("登录成功");
                        user.mobile = getText(etMobile);
                        UserService.save(user);
                        HomeActivity.start(LoginAct.this);
                        finish();
                    }
                });
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

    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean clearAll) {
        Intent intent = new Intent(context, LoginAct.class);
        if (clearAll) {
            Preferences.saveUserToken("");
            User user = new User();
            user.mobile = UserService.getMobile();
            UserService.save(user);
            LogoutHelper.logout();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
