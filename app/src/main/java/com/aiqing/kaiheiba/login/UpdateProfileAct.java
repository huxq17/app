package com.aiqing.kaiheiba.login;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aiqing.kaiheiba.HomeActivity;
import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.UserApi;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.aiqing.kaiheiba.utils.DatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpdateProfileAct extends BaseActivity {
    private TextView etNickName, etBirth, etGender;
    private DatePicker datePicker;
    int gender = -1;
    private List<String> mGenders = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        datePicker = new DatePicker(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                setDate(calendar);
            }
        });
        mGenders.add("保密");
        mGenders.add("男");
        mGenders.add("女");
        initView();
    }

    public void setDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        etBirth.setText(sdf.format(calendar.getTime()));
    }

    private void initView() {
        etNickName = findViewById(R.id.et_mobile);
        etGender = findViewById(R.id.et_pass);
        etBirth = findViewById(R.id.et_mobile_code);
        etBirth.setOnClickListener(this);
        etGender.setOnClickListener(this);
    }

    public void retrievePassBack(View v) {
        if (isEmpty(etNickName) || isEmpty(etGender) || isEmpty(etNickName)) {
            toast("请完善信息");
            if (isEmpty(etNickName)) {
                etNickName.requestFocus();
            } else if (isEmpty(etGender)) {
                etGender.requestFocus();
            } else if (isEmpty(etNickName)) {
                etNickName.requestFocus();
            }
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("sign", "");
        params.put("nickname", getText(etNickName));
        params.put("gender", gender + "");
        params.put("born", getText(etBirth));
        params.put("province", "");
        ApiManager.INSTANCE.getApi(UserApi.class).updateProf(params)
                .compose(RxSchedulers.<UserApi.Bean>compose())
                .subscribe(new BaseObserver<Object>(this, "注册中...") {
                    @Override
                    protected void onSuccess(Object o) {
                        toast("注册成功");
                        finish();
                        HomeActivity.start(UpdateProfileAct.this);
                    }

                    @Override
                    protected void onFailed(String msg) {
                        super.onFailed(msg);
                        toast(msg);
                    }
                });
    }

    private void focusSth(View view) {
        if (!isEmpty(etNickName)) {
            view.requestFocusFromTouch();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        switch (id) {
            case R.id.et_mobile_code:
                datePicker.show();
                focusSth(etBirth);
                break;
            case R.id.et_pass:
                focusSth(etGender);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        com.aiqing.imagepicker.R.layout.list_item,
                        mGenders
                );
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DefaultExplainingPermissionsTheme /*android.R.style.Theme_Holo_Light_Dialog*/);
                builder.setTitle("选择性别");

                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {
                        gender = index;
                        etGender.setText(mGenders.get(index));
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,
                                        int i) {
                        dialogInterface.dismiss();
                    }
                });

                final AlertDialog dialog = builder.create();

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(@NonNull final DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, UpdateProfileAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
