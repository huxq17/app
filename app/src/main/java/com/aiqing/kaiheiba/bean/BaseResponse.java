package com.aiqing.kaiheiba.bean;

import com.aiqing.kaiheiba.api.Code;

public class BaseResponse<E> {

    private int code;
    private String message;
    private E data;

    public boolean isSuccess() {
        return code == Code.OK;
    }

    public boolean isTokenInvalidate() {
        return code == Code.ERROR_TOKEN_INVALIDATE;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
