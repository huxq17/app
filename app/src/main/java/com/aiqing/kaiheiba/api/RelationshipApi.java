package com.aiqing.kaiheiba.api;

import com.aiqing.kaiheiba.bean.AccountBean;
import com.aiqing.kaiheiba.bean.BaseResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RelationshipApi {

    @POST("/follow/follower/list")
    Observable<RelationshipApi.Bean> obtainFans();

    @POST("/follow/following/list")
    Observable<RelationshipApi.Bean> obtainFollowers();

    @FormUrlEncoded
    @POST("/follow/del")
    Observable<BaseResponse<Object>> unfollow(@Field("account_follow_id") int followid);

    @FormUrlEncoded
    @POST("/follow/add")
    Observable<BaseResponse<Object>> follow(@Field("account_follow_id") int followid);

    class Bean extends BaseResponse<List<Bean.DataBean>> {

        public static class DataBean {
            private int AccountFollowId;
            private int AccountId;
            private String CreateAt;
            private int DeleteFlag;
            private int ID;
            private int Status;
            private AccountBean account;

            public int getAccountFollowId() {
                return AccountFollowId;
            }

            public int getAccountId() {
                return AccountId;
            }

            public void setAccountId(int AccountId) {
                this.AccountId = AccountId;
            }

            public String getCreateAt() {
                return CreateAt;
            }

            public void setCreateAt(String CreateAt) {
                this.CreateAt = CreateAt;
            }

            public int getDeleteFlag() {
                return DeleteFlag;
            }

            public void setDeleteFlag(int DeleteFlag) {
                this.DeleteFlag = DeleteFlag;
            }

            public int getID() {
                return ID;
            }

            public void setID(int ID) {
                this.ID = ID;
            }

            public int getStatus() {
                return Status;
            }

            public void setStatus(int Status) {
                this.Status = Status;
            }

            public AccountBean getAccount() {
                return account;
            }

            public void setAccount(AccountBean account) {
                this.account = account;
            }

        }
    }
}
