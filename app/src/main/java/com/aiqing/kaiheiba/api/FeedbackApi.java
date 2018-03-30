package com.aiqing.kaiheiba.api;

import com.aiqing.kaiheiba.bean.SimpleBean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedbackApi {
    @FormUrlEncoded
    @POST("/advice/add")
    Observable<SimpleBean> feedback(@Field("content") String avatar);

}
