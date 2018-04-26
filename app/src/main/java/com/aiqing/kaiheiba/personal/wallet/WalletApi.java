package com.aiqing.kaiheiba.personal.wallet;

import com.aiqing.kaiheiba.bean.BaseResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface WalletApi {
    @FormUrlEncoded
    @POST("/query_user_info")
    Observable<WalletBean> queryUserInfo(@Field("token") String search);

    @FormUrlEncoded
    @POST("/member/wealth")
    Observable<RecordBean> queryRecordInfo(@Field("date") String date);

    @GET("/recharge/list")
    Observable<ChargeListBean> queryChargeList();

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

    class RecordBean extends BaseResponse<RecordBean.DataBean> {
        public static class DataBean {
            private List<ResultBean> result;

            public List<ResultBean> getResult() {
                return result;
            }

            public static class ResultBean {
                /**
                 * AccountId : 114
                 * Amount : 90
                 * AppId : 211502
                 * AppOrderId : 0
                 * CreatedAt : 2018-04-19T16:38:01+08:00
                 * Desc : 陪玩订单收款
                 * FromTo : 113
                 * ID : 3
                 * Type : 4 // 3陪玩订单支付 4陪玩订单收款 5陪玩订单退款 6帖子打赏支付 7帖子打赏收款
                 */

                private String AccountId;
                private String Amount;
                private String AppId;
                private String AppOrderId;
                private String CreatedAt;
                private String Desc;
                private String FromTo;
                private int ID;
                private String Type;

                public String getAccountId() {
                    return AccountId;
                }

                public String getAmount() {
                    if (isIncome()) {
                        return "+收入 " + Amount;
                    } else {
                        return "-支出 " + Amount;
                    }
                }

                public String getAppId() {
                    return AppId;
                }

                public String getAppOrderId() {
                    return AppOrderId;
                }

                public String getCreatedAt() {
                    return CreatedAt;
                }

                public String getDesc() {
                    return Desc;
                }

                public String getFromTo() {
                    return FromTo;
                }

                public int getID() {
                    return ID;
                }

                public boolean isIncome() {
                    return !Type.equals("3") && !Type.equals("6");
                }

                public String getType() {
                    return Type;
                }

                public void setType(String Type) {
                    this.Type = Type;
                }
            }
        }
    }
}
