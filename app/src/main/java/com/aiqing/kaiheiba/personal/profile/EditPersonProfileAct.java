package com.aiqing.kaiheiba.personal.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aiqing.imagepicker.ImagePicker;
import com.aiqing.kaiheiba.App;
import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.OssToken;
import com.aiqing.kaiheiba.api.UserApi;
import com.aiqing.kaiheiba.bean.AccountBean;
import com.aiqing.kaiheiba.common.BaseActivity;
import com.aiqing.kaiheiba.imageloader.ImageLoader;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.aiqing.kaiheiba.utils.DatePicker;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.buyi.huxq17.serviceagency.ServiceAgency;
import com.lljjcoder.style.citylist.CityListSelectActivity;
import com.lljjcoder.style.citylist.bean.CityInfoBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import user.UserService;

import static com.aiqing.imagepicker.utils.MediaUtils.fileScan;

public class EditPersonProfileAct extends BaseActivity {
    private Uri imageUri;

    public File tempFile;
    private ImageView ivAvatar;
    private TextView tvArea, tvDate, tvName;
    private DatePicker datePicker;
    private EditText etSign, etNickName;
    private Button btSubmit;
    private RadioGroup rgGender;
    private ImagePicker imagePicker;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("我的资料");
        setContentView(R.layout.activity_edit_profile);
        findViewById(R.id.prof_change_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog();
            }
        });
        findViewById(R.id.prof_area_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCity();
            }
        });
        findViewById(R.id.prof_date_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });
        tvName = findViewById(R.id.prof_name);

        datePicker = new DatePicker(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                setDate(calendar);
            }
        });
        ivAvatar = findViewById(R.id.prof_avatar);
        tvArea = findViewById(R.id.prof_show);
        tvDate = findViewById(R.id.prof_birthday_show);
        etSign = findViewById(R.id.prof_sign_input);
        etNickName = findViewById(R.id.prof_edit_nickname);
        btSubmit = findViewById(R.id.prof_submit);
        rgGender = findViewById(R.id.rg_gender);
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });
        setDate(Calendar.getInstance());
        setAvatar(UserService.getAvatar());
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(etSign) || isEmpty(etNickName)) {
                    toast("请完善资料");
                    return;
                }
                Map<String, String> params = new HashMap<>();
                params.put("sign", getText(etSign));
                params.put("nickname", getText(etNickName));
                String gender = "0";
                int checkedId = rgGender.getCheckedRadioButtonId();
                switch (checkedId) {
                    case R.id.rb_gender_male:
                        gender = "1";
                        break;
                    case R.id.rb_gender_female:
                        gender = "2";
                        break;
                    case R.id.rb_gender_secret:
                        gender = "0";
                        break;
                }
                params.put("gender", gender);
                params.put("born", getText(tvDate));
                params.put("province", getText(tvArea));
                ApiManager.INSTANCE.getApi(UserApi.class).updateProf(params)
                        .compose(RxSchedulers.<UserApi.Bean>compose())
                        .subscribe(new BaseObserver<Object>(EditPersonProfileAct.this,"保存中...") {
                            @Override
                            protected void onSuccess(Object o) {
                                toast("保存成功");
                                finish();
                            }

                            @Override
                            protected void onFailed(String msg) {
                                super.onFailed(msg);
                                toast(msg);
                            }
                        });
            }
        });
        obtainProf();
        imagePicker = new ImagePicker(this, new ImagePicker.OnImagePickerListener() {
            @Override
            public void onError(String error) {

            }

            @Override
            public void onSuccess(Bundle response) {
                Uri uri = Uri.parse(response.getString("uri"));
                if (uri != null) {
                    imageUri = uri;
                }
                try {
                    tempFile = new File(getPath(imageUri));
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                toastL("头像上传中...");
                ApiManager.INSTANCE.getApi(OssToken.Api.class).getToken()
                        .subscribeOn(Schedulers.io())
                        .flatMap(new Function<OssToken.Bean, ObservableSource<PutObjectResult>>() {
                            @Override
                            public ObservableSource<PutObjectResult> apply(OssToken.Bean bean) throws Exception {
                                final OssToken.OSSBean ossBean = bean.getData();
                                return updateAvatar(ossBean);
                            }
                        })
                        .flatMap(new Function<PutObjectResult, ObservableSource<UserApi.Bean>>() {
                            @Override
                            public ObservableSource<UserApi.Bean> apply(PutObjectResult putObjectResult) throws Exception {
                                String avatarUrl = OssToken.Client.getObjectKey("avatar", tempFile.getName());
                                return ApiManager.INSTANCE.getApi(UserApi.class).uploadAvatar(avatarUrl);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseObserver<Object>(EditPersonProfileAct.this,"上传中...") {
                            @Override
                            protected void onSuccess(Object bean) {
                                toast("头像上传成功");
                                String avatarUrl = OssToken.Client.getObjectKey("avatar", tempFile.getName());
                                setAvatar(avatarUrl);
                                if (tempFile.getName().contains("resize") && tempFile.exists()) {
                                    tempFile.delete();
                                    fileScan(App.getContext(), tempFile.getAbsolutePath());
                                }
                            }

                            @Override
                            protected void onFailed(String msg) {
                                super.onFailed(msg);
                                log(msg);
                                toast(msg);
                                if (tempFile.exists()) {
                                    tempFile.delete();
                                    fileScan(App.getContext(), tempFile.getAbsolutePath());
                                }
                            }
                        });
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void setAvatar(String url) {
        if (!TextUtils.isEmpty(url)) {
            ServiceAgency.getService(ImageLoader.class).loadImage(OssToken.Client.OSSDomain + url, ivAvatar);
            UserService.setAvatar(OssToken.Client.OSSDomain + url);
        }
    }

    private void setSign(String sign) {
        etSign.setText(sign);
        focusableAfterClick(etSign);
    }

    private void setNickName(String nickName) {
        etNickName.setText(nickName);
        focusableAfterClick(etNickName);
    }

    private void focusableAfterClick(EditText editText) {
//        editText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                etNickName.setFocusable(true);
//            }
//        });
//        editText.setFocusable(false);
        editText.setSelection(getText(editText).length());
    }

    public void setDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tvDate.setText(sdf.format(calendar.getTime()));
    }

    public void pickCity() {
        Intent intent = new Intent(this, CityListSelectActivity.class);
        startActivityForResult(intent, CityListSelectActivity.CITY_SELECT_RESULT_FRAG);
    }

    public void pickDate() {
        datePicker.show();
    }


    public void popupDialog() {
        imagePicker.showImagePicker();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CityListSelectActivity.CITY_SELECT_RESULT_FRAG:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    Bundle bundle = data.getExtras();
                    CityInfoBean cityInfoBean = bundle.getParcelable("cityinfo");
                    if (null == cityInfoBean) {
                        return;
                    }
                    tvArea.setText(cityInfoBean.getName());
                }
                break;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public void obtainProf() {
        ApiManager.INSTANCE.getApi(UserApi.class).obtainProf()
                .compose(RxSchedulers.<ProfileBean>compose())
                .subscribe(new BaseObserver<AccountBean>(this) {
                    @Override
                    protected void onSuccess(AccountBean accountBean) {
                        setAvatar(accountBean.getAvatar());
                        setSign(accountBean.getSign());
                        setNickName(accountBean.getNickname());
                        int gender = accountBean.getGender();
                        if (gender == 1) {
                            rgGender.check(R.id.rb_gender_male);
                        } else if (gender == 2) {
                            rgGender.check(R.id.rb_gender_female);
                        } else if (gender == 0) {
                            rgGender.check(R.id.rb_gender_secret);
                        }
                        String born = accountBean.getBorn();
                        if (!isEmpty(born)) {
                            born = born.substring(0, 10);
                            tvDate.setText(born);
                        }
                        tvArea.setText(accountBean.getProvince());
                        tvName.setText("UID:" + accountBean.getAccountId());
                    }
                });
    }


    public Observable<PutObjectResult> updateAvatar(final OssToken.OSSBean ossBean) {
        return Observable.create(new ObservableOnSubscribe<PutObjectResult>() {
            @Override
            public void subscribe(ObservableEmitter<PutObjectResult> emitter) throws Exception {
                OSS oss = OssToken.Client.init(EditPersonProfileAct.this.getApplicationContext(), ossBean);
                PutObjectRequest put = new PutObjectRequest("aiqing-lianyun",
                        OssToken.Client.getObjectKey("avatar", tempFile.getName()), tempFile.getPath());
                try {
                    PutObjectResult putResult = oss.putObject(put);
                    emitter.onNext(putResult);
                } catch (ClientException e) {
                    emitter.onError(e);
                } catch (ServiceException e) {
                    emitter.onError(e);
                    // 服务异常
                    Log.e("RequestId", e.getRequestId());
                    Log.e("ErrorCode", e.getErrorCode());
                    Log.e("HostId", e.getHostId());
                    Log.e("RawMessage", e.getRawMessage());
                }
            }
        });

    }
}
