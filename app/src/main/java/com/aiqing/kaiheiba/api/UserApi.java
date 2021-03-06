package com.aiqing.kaiheiba.api;

import com.aiqing.kaiheiba.bean.AccountBean;
import com.aiqing.kaiheiba.bean.BaseResponse;
import com.aiqing.kaiheiba.personal.profile.ProfileBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserApi {

    @FormUrlEncoded
    @POST("/member/avatar/update")
    Observable<UserApi.Bean> uploadAvatar(@Field("avatar") String avatar);

    @FormUrlEncoded
    @POST("/member/info/update")
    Observable<UserApi.Bean> updateProf(@FieldMap Map<String, String> params);

    @POST("/member/info")
    Observable<ProfileBean> obtainProf();

    @FormUrlEncoded
    @POST("/member/search")
    Observable<SearchBean> search(@Field("search") String search);

    @POST("/member/invite/info")
    Observable<BaseResponse<AccountBean>> getInvitors();


    @FormUrlEncoded
    @POST("/member/real/auth")
    Observable<BaseResponse<Object>> verifyTrueName(@Field("token") String token, @Field("real_name") String real_name, @Field("id_card") String id_card);


    class Bean extends BaseResponse<Object> {
    }

    class SearchBean extends BaseResponse<List<AccountBean>> {

    }
}
