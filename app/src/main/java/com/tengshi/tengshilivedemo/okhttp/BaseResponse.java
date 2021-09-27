package com.tengshi.tengshilivedemo.okhttp;

import com.google.gson.JsonObject;

/**
 * 统一响应
 * @param <T>
 */
public class BaseResponse<T> {
    private int code;
    private T data;
    private String exception;
    private JsonObject extData;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public JsonObject getExtData() {
        return extData;
    }

    public void setExtData(JsonObject extData) {
        this.extData = extData;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
