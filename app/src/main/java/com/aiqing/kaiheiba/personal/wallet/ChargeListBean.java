package com.aiqing.kaiheiba.personal.wallet;


import com.aiqing.kaiheiba.bean.BaseResponse;

import java.util.List;

public class ChargeListBean extends BaseResponse<ChargeListBean.DataBean> {

    public static class DataBean {
        private List<AndroidBean> android;

        public List<AndroidBean> getAndroid() {
            return android;
        }

        public static class AndroidBean {
            private int ID;
            private int Number;
            private int Price;
            private int System;

            public int getID() {
                return ID;
            }

            public void setID(int ID) {
                this.ID = ID;
            }

            public String getNumber() {
                return new StringBuilder().append("零食包").append(Number).append("个").toString();
            }

            public void setNumber(int Number) {
                this.Number = Number;
            }

            public int getPrice() {
                return Price;
            }

            public void setPrice(int Price) {
                this.Price = Price;
            }

            public int getSystem() {
                return System;
            }

            public void setSystem(int System) {
                this.System = System;
            }
        }
    }
}
