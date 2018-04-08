package com.aiqing.kaiheiba.api;

import com.aiqing.kaiheiba.bean.BaseResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface VersionApi {
    @GET("/app/version")
    Observable<Bean> getAppVersion();

    class Bean extends BaseResponse<Bean.DataBean> {
        public static class DataBean {
            /**
             * android : {"url":"http://download.17kaiheiba.com/app/kaiheiba.apk","version":"1.0.0"}
             * ios : {"url":"http://itunes.apple.com/cn/lookup?id=1366109559","version":"1.0.0"}
             */

            private AndroidBean android;

            public AndroidBean getAndroid() {
                return android;
            }

            public void setAndroid(AndroidBean android) {
                this.android = android;
            }

            public static class AndroidBean {
                /**
                 * url : http://download.17kaiheiba.com/app/kaiheiba.apk
                 * version : 1.0.0
                 */

                private String url;
                private String version;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getVersion() {
                    return version;
                }

                public void setVersion(String version) {
                    this.version = version;
                }
            }

        }
    }
}
