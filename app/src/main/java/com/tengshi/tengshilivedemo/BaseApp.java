package com.tengshi.tengshilivedemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.qm.qmclass.qmmanager.QMClassManager;
import com.tengshi.tengshilivedemo.utils.CrashHandler;
import com.tengshi.tengshilivedemo.utils.PushHelper;
import com.tengshi.tengshilivedemo.utils.SharedPreferencesUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * Created by lz on 2020/11/12.
 */
public class BaseApp extends Application {
    private static Context context;
    private static Application application;
    private static Activity mactivity;
    public static BaseApp instance;
    private static QMClassManager qmClassManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application=this;
        SharedPreferencesUtils.getInstance(this,"qm_data");

        //预初始化
        PushHelper.preInit(this);
        //正式初始化
        initPushSDK();

        qmClassManager=QMClassManager.getInstance();
        qmClassManager.init(this);
    }

    public static QMClassManager getQMClassManager() {
        return qmClassManager;
    }

    public static BaseApp getInstance(){

        if(instance == null){

            instance = new BaseApp();

        }

        return instance;

    }

    /**
     * 初始化推送SDK，在用户隐私政策协议同意后，再做初始化
     */
    private void initPushSDK() {
        /*
         * 当用户同意隐私政策协议时，直接进行初始化；
         * 当用户未同意隐私政策协议时，需等用户同意后，再通过PushHelper.init(...)方法进行初始化。
         */
        boolean agreed = true;
        if (agreed && PushHelper.isMainProcess(this)) {
            //建议在线程中执行初始化
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushHelper.init(getApplicationContext());
                }
            }).start();
        }
    }

    public static BaseApp get(Context context){
        return (BaseApp) context.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
    public static Application getApplication() {
        return application;
    }

    public static Activity getActivity() {
        return mactivity;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
