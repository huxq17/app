package com.aiqing.kaiheiba.personal.wallet;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WalletApi {
    @FormUrlEncoded
    @POST("/query_user_info")
    Observable<WalletBean> queryUserInfo(@Field("token") String search);

    class WalletBean {

        /**
         * money : 0.00
         * mrc : 0.000000
         * status : 0
         */

        private String money;
        private String mrc;
        private int status;

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getMrc() {
            return mrc;
        }

        public void setMrc(String mrc) {
            this.mrc = mrc;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
