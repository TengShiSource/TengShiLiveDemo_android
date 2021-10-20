package com.qm.qmdemo.okhttp;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.qm.qmdemo.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        String appKey= SharedPreferencesUtils.getString("appId","");
        String appSecret= SharedPreferencesUtils.getString("appSecret","");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sign = md5(appKey+appSecret+timestamp);
        Request originalRequest = chain.request();
        //key的话以后台给的为准，我这边是叫token
        Request updateRequest = originalRequest.newBuilder()
                .addHeader("appKey",appKey)
                .addHeader("timestamp",timestamp)
                .addHeader("sign",sign.toUpperCase())
                .build();
        return chain.proceed(updateRequest);

    }
    @NonNull
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
