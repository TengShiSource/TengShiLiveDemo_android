package com.qm.qmclass.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.tencent.tiw.logger.http.HttpClient;

import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 系统工具类
 * Created by lz on 2020-12-22.

 */
public class GetPictureUtil {
    private static Bitmap picture;

    public static Bitmap getNetPicture(final String pictureUrl){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = null;
                try {
                    //创建URL
                    URL url1 = new URL(pictureUrl);
                    //打开链接
                    HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
                    //设置访问方式
                    urlConnection.setRequestMethod("GET");
                    //设置超时
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    //返回响应码
                    int responseCode = urlConnection.getResponseCode();
                    //判断响应是否成功
                    if (responseCode==200){
                        //将网络路径转换成存入bitmap
                        bm = BitmapFactory.decodeStream(urlConnection.getInputStream());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                picture=bm;
            }
        }).start();
        return picture;
    }
    private void showToast(String words) {
        System.out.println(words);
    }
}
