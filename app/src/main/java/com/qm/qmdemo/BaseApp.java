package com.qm.qmdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.qm.qmclass.qmmanager.QMClassManager;
import com.qm.qmdemo.utils.CrashHandler;
import com.qm.qmdemo.utils.PushHelper;
import com.qm.qmdemo.utils.SharedPreferencesUtils;


/**
 * Created by lz on 2020/11/12.
 */
public class BaseApp extends Application {
    private static Context context;
    private static Application application;
    private static Activity mactivity;
    public static BaseApp instance;
    private CrashHandler handler;
//    private String wxCode="";
    private static QMClassManager qmClassManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application=this;
        handler = new CrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
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
