package com.aiqing.kaiheiba.personal.wallet;

import android.text.TextUtils;

import com.aiqing.kaiheiba.bean.BaseResponse;

import java.math.BigDecimal;
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

    @FormUrlEncoded
    @POST("/recharge/create")
    Observable<OrderBean> createOrder(@Field("recharge_id") int rechargeId);

    class WalletBean {

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

    class OrderBean extends BaseResponse<OrderBean.DataBean> {
        public static class DataBean {
            /**
             * app_attach :
             * app_id : 211543
             * app_notify_url : http://116.62.139.161/recharge/notify
             * app_order_id : 2018042709361855048
             * app_user_id : 1
             * app_username : 123456789
             * body :
             * order_name : 零食包60个
             * price : 600
             * sign : IVRmBRQGw05sDaEtAZKXA4vdLSrFlpeIv50/q3jPXoi749MYcMUidEMV5yXs5t9f190HqyPJziClV8OqiBzHMHOI33LVybmbLTGR+mdr9sVKh8XYh1VUpk7DbAK99zvhkNoi/rPgBTXbwDmGxHg7U803a+N/zS+M8/L41wV3L+T87VHJS6ZT1qkHkCtjd8rswTFhewuyf2zaHpQwYl3o16bQBSQVTrUgvAFkT6bAiMSbZ1O1mxTvU03b0F8p31aTJxN67Pl1yLI/ZCVsitYwBUm+GVwbuf7DifLV298gyw05egJW1Tit/bSNjAIqpUMpKHhce4kckeEKP3fQ1r/uMw==
             * subject : 零食包60个
             * total_amount : 600
             */

            private String app_attach;
            private String app_id;
            private String app_notify_url;
            private String app_order_id;
            private String app_user_id;
            private String app_username;
            private String body;
            private String order_name;
            private String price;
            private String sign;
            private String subject;
            private String total_amount;

            public String getApp_attach() {
                return app_attach;
            }


            public String getApp_id() {
                return app_id;
            }

            public String getApp_notify_url() {
                return app_notify_url;
            }

            public String getApp_order_id() {
                return app_order_id;
            }

            public String getApp_user_id() {
                return app_user_id;
            }

            public String getApp_username() {
                return app_username;
            }

            public String getBody() {
                return body;
            }

            public String getOrder_name() {
                return order_name;
            }

            public String getPrice() {
                return price;
            }

            public String getSign() {
                return sign;
            }

            public String getSubject() {
                return subject;
            }

            public String getTotal_amount() {
                return total_amount;
            }
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
                    if (!TextUtils.isEmpty(Amount) && !TextUtils.isDigitsOnly(Amount)) {
                        return "0";
                    }
                    int amount = Integer.parseInt(Amount);
                    String result = BigDecimal.valueOf(amount).
                            divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_DOWN) + "元";
                    if (isIncome()) {
                        return "+收入 " + result;
                    } else {
                        return "-支出 " + result;
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
