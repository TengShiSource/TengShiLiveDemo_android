package com.qm.qmdemo.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {
    /**
     * 网络访问要求singleton
     */
    private static OkHttpUtils instance;
    // 必须要用的okhttpclient实例,在构造器中实例化保证单一实例
    private OkHttpClient mOkHttpClient;
    public static final MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FILETYPE = MediaType.parse("multipart/form-data");
    private Handler mHandler;
    private Gson mGson;
    private String mtoken="";
    private OkHttpUtils() {
        /**
         * okHttp3中超时方法移植到Builder中
         */
        mOkHttpClient = (new OkHttpClient()).newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new LogInterceptor())
                .build();
        mHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }
    public static OkHttpUtils getInstance() {
        if (instance == null) {
            synchronized (OkHttpUtils.class) {
                if (instance == null) {
                    instance = new OkHttpUtils();
                }
            }
        }
        return instance;
    }
    public void setToken(String token){
        mtoken=token;
    }
    /**
     * 对外提供的Get方法访问
     * @param url
     * @param callBack
     */
    public void Get(String url, MyCallBack callBack) {
        /**
         * 通过url和GET方式构建Request
         */
        Request request = bulidRequestForGet(url);
        /**
         * 请求网络的逻辑
         */
        requestNetWork(request, callBack);
    }
    /**
     * 对外提供的Post方法访问
     * @param url
     * @param parms: 提交内容为表单数据
     * @param callBack
     */
    public void PostWithFormData(String url, Map<String, String> parms, MyCallBack callBack) {
        /**
         * 通过url和POST方式构建Request
         */
        Request request = bulidRequestForPostByForm(url, parms);
        /**
         * 请求网络的逻辑
         */
        requestNetWork(request, callBack);
    }
    /**
     * 对外提供的Post方法访问
     * @param url
     * @param json: 提交内容为json数据
     * @param callBack
     */
    public void PostWithJson(String url, String json, MyCallBack callBack) {
        /**
         * 通过url和POST方式构建Request
         */
        Request request = bulidRequestForPostByJson(url, json);
        /**
         * 请求网络的逻辑
         */
        requestNetWork(request, callBack);
    }
    /**
     * 对外提供的Post方法访问
     * @param url
     * @param file: 提交内容为file数据
     * @param callBack
     */
    public void PostWithFile(String url, File file, MyCallBack callBack) {
        /**
         * 通过url和POST方式构建Request
         */
        Request request = bulidRequestForPostByFile(url, file);
        /**
         * 请求网络的逻辑
         */
        requestNetWork(request, callBack);
    }
    /**
     * POST方式构建Request {file}
     * @param url
     * @param file
     * @return
     */
    private Request bulidRequestForPostByFile(String url, File file) {
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile = RequestBody.create(FILETYPE, file);
//        //和后端约定好Key，这里的partName是用file
//        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), requestFile)
                .build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }
    /**
     * POST方式构建Request {json}
     * @param url
     * @param json
     * @return
     */
    private Request bulidRequestForPostByJson(String url, String json) {
        RequestBody body = RequestBody.create(JSONTYPE, json);
        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }
    /**
     * POST方式构建Request {Form}
     * @param url
     * @param parms
     * @return
     */
    private Request bulidRequestForPostByForm(String url, Map<String, String> parms) {
        FormBody.Builder builder = new FormBody.Builder();
        if (parms != null) {
            for (Map.Entry<String, String> entry :
                    parms.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody body = builder.build();
        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }
    /**
     * GET方式构建Request
     * @param url
     * @return
     */
    private Request bulidRequestForGet(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }
    private void requestNetWork(final Request request, final MyCallBack<Object> callBack) {
        /**
         * 处理连网逻辑，此处只处理异步操作enqueue
         */
        callBack.onLoadingBefore(request);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.e("Error",e.toString());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(request, e);
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) {
                if (response.isSuccessful()) {
                    String resultStr = response.body().string();
                    if (callBack.mType == String.class) {
                        // 如果想要返回字符串 直接返回就行
                        String finalResultStr = resultStr;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(finalResultStr);
                            }
                        });
                    } else {
                        // 需要返回解析好的javaBean集合
                        try {
                            // 此处暂时写成object，使用时返回具体的带泛型的集合
                            final Object obj = mGson.fromJson(resultStr, callBack.mType);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onSuccess(obj);
                                }
                            });
                        } catch (Exception e) {
                            Log.e("Error",e.toString());
                            // 解析错误时
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onError(response);
                                }
                            });

                        }
                    }
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onError(response);
                        }
                    });
                }
            }
        });
    }
}
