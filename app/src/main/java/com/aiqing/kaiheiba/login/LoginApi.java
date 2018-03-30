package com.aiqing.kaiheiba.login;

import com.aiqing.kaiheiba.bean.BaseResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import user.User;

public interface LoginApi {

    @FormUrlEncoded
    @POST("/verify_code")
    Observable<Bean> obtainMobileCode(@Field("mobile") String mobile, @Field("type") int type);

    @POST("/member/regist")
    @FormUrlEncoded
    Observable<Bean> register(@FieldMap Map<String, String> params);

    @POST("/member/login")
    @FormUrlEncoded
    Observable<Bean> login(@FieldMap Map<String, String> params);

    @POST("/member/password/reset")
    @FormUrlEncoded
    Observable<Bean> updatePass(@FieldMap Map<String, String> params);

    @POST("/member/password/forgot")
    @FormUrlEncoded
    Observable<Bean> resetPass(@FieldMap Map<String, String> params);

    class Bean extends BaseResponse<User> {
    }
}
