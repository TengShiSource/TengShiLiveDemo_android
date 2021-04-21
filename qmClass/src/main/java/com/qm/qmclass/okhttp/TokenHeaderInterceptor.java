package com.qm.qmclass.okhttp;

import com.qm.qmclass.base.Constants;
import com.qm.qmclass.utils.SharedPreferencesUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        String token = SharedPreferencesUtils.getString(Constants.USER_Token,"");
//        String token = SpUtils.getToken();
        if (token.isEmpty()) {
            Request originalRequest = chain.request();
            return chain.proceed(originalRequest);
        }else {
            Request originalRequest = chain.request();
            //key的话以后台给的为准，我这边是叫token
            Request updateRequest = originalRequest.newBuilder().addHeader("Authorization","Bearer "+token).build();
            return chain.proceed(updateRequest);
        }
    }
}
