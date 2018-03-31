package com.aiqing.kaiheiba.personal.profile;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.aiqing.kaiheiba.widget.ActionSheet;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.buyi.huxq17.serviceagency.ServiceAgency;
import com.huxq17.xprefs.LogUtils;
import com.lljjcoder.style.citylist.CityListSelectActivity;
import com.lljjcoder.style.citylist.bean.CityInfoBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import user.UserService;

public class EditPersonProfileAct extends BaseActivity {
    public static final int PHOTO_REQUEST_CAREMA = 1;
    public static final int CROP_PHOTO = 2;
    String[] perms = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final int RC_CAMERA = 1;
    private static final int RC_SDCARD = 2;
    private Uri imageUri;

    public static File tempFile;
    private ImageView ivAvatar;
    private TextView tvArea, tvDate;
    private DatePicker datePicker;
    private EditText etSign, etNickName;
    private Button btSubmit;
    private RadioGroup rgGender;

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
                        .subscribe(new BaseObserver<Object>() {
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
    }

    private void setAvatar(String url) {
        if (!TextUtils.isEmpty(url)) {
            ServiceAgency.getService(ImageLoader.class).loadImage(url, ivAvatar);

            UserService.setAvatar(url);
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

    @AfterPermissionGranted(RC_CAMERA)
    private void onCameraGranted() {
        takePhoto();
    }

    @AfterPermissionGranted(RC_SDCARD)
    private void onSdcardGranted() {
        log("onSdcardGranted");
        openAlbum();
    }

    public void popupDialog() {
        ActionSheet.createBuilder(this)
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("拍照", "从相册中选取")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {

                    @Override
                    public void onOtherButtonClick(int index) {

                        switch (index) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                openAlbum();
                                break;
                            case 2:
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void takePhoto() {
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "拍照需要相机和读取外部存储权限", RC_CAMERA, perms);
            return;
        }
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    private void openAlbum() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "需要读取外部存储权限", RC_SDCARD, Manifest.permission.CAMERA);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CROP_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO);
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            imageUri = uri;
                        }
                        LogUtils.e("imageuri=" + imageUri);
                        try {
                            tempFile = new File(getPath(imageUri));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
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
                                    String avatarUrl = OssToken.Client.OSSDomain + tempFile.getName();
                                    return ApiManager.INSTANCE.getApi(UserApi.class).uploadAvatar(avatarUrl);
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new BaseObserver<Object>() {
                                @Override
                                protected void onSuccess(Object bean) {
                                    toast("头像上传成功");
                                    String avatarUrl = OssToken.Client.OSSDomain + tempFile.getName();
                                    setAvatar(avatarUrl);
                                }

                                @Override
                                protected void onFailed(String msg) {
                                    super.onFailed(msg);
                                    log(msg);
                                    toast(msg);
                                }
                            });
                }
                break;
        }
    }

    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
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
                .subscribe(new BaseObserver<AccountBean>() {
                    @Override
                    protected void onSuccess(AccountBean accountBean) {
                        setAvatar(accountBean.getAvatar());
                        setSign(accountBean.getSign());
                        setNickName(accountBean.getNickname());
                        String gender = accountBean.getGender();
                        if (gender.equals("1")) {
                            rgGender.check(R.id.rb_gender_male);
                        } else if (gender.equals("2")) {
                            rgGender.check(R.id.rb_gender_female);
                        } else if (gender.equals("0")) {
                            rgGender.check(R.id.rb_gender_secret);
                        }
                        tvDate.setText(accountBean.getBorn());
                        tvArea.setText(accountBean.getProvince());
                    }
                });
    }

    public Observable<PutObjectResult> updateAvatar(final OssToken.OSSBean ossBean) {
        return Observable.create(new ObservableOnSubscribe<PutObjectResult>() {
            @Override
            public void subscribe(ObservableEmitter<PutObjectResult> emitter) throws Exception {
                OSS oss = OssToken.Client.init(EditPersonProfileAct.this.getApplicationContext(), ossBean);
                PutObjectRequest put = new PutObjectRequest("aiqing-lianyun", tempFile.getName(), tempFile.getPath());
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
