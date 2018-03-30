package com.aiqing.kaiheiba.api;


import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;

import io.reactivex.Observable;
import retrofit2.http.POST;

public interface OssToken {
    interface Api {
        String Path = "oss/token";

        @POST(Path)
        Observable<Bean> getToken();
//        Observable<Bean> getToken(@Header("X-Auth-UserId") String id);
    }

    class Client {
        public static String OSSDomain = "http://aiqing-lianyun.oss-cn-hangzhou.aliyuncs.com/";
        public static OSS init(Context context, OssToken.OSSBean ossBean) {
//            OssToken.OSSBean ossBean = XPrefs.get(OssToken.OSSBean.class);
            String AccessKeyId = ossBean.getAccessKeyId();
            String SecretKeyId = ossBean.getAccessKeySecret();
            String SecurityToken = ossBean.getSecurityToken();

            String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";

            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
//            OSSLog.enableLog(); //这个开启会支持写入手机sd卡中的一份日志文件位置在SDCard_path\OSSLog\logs.csv

            OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(AccessKeyId, SecretKeyId, SecurityToken);

            OSS oss = new OSSClient(context, endpoint, credentialProvider, conf);
            return oss;
        }
    }

    class Bean {
        private int code;
        private String message;
        private OSSBean data;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public OSSBean getData() {
            return data;
        }


    }

    class OSSBean {

        private String AccessKeyId;
        private String AccessKeySecret;
        private String Expiration;
        private String SecurityToken;
        private int StatusCode;

        public String getAccessKeyId() {
            return AccessKeyId;
        }

        public String getAccessKeySecret() {
            return AccessKeySecret;
        }

        public String getExpiration() {
            return Expiration;
        }

        public String getSecurityToken() {
            return SecurityToken;
        }

        public int getStatusCode() {
            return StatusCode;
        }
    }
}
