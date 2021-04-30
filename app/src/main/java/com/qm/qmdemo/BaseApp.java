package com.qm.qmdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.android.tony.defenselib.DefenseCrash;
import com.android.tony.defenselib.handler.IExceptionHandler;
import com.qm.qmclass.qmmanager.QMClassManager;
import com.qm.qmclass.utils.CrashHandler;


/**
 * Created by lz on 2020/11/12.
 */
public class BaseApp extends Application implements IExceptionHandler {
    private static Context context;
    private static Application application;
    private static Activity mactivity;
    public static BaseApp instance;
    private Handler handler;
//    private String wxCode="";
    private static QMClassManager qmClassManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CrashHandler.getInstance().init(this);
        application=this;
        // defense_crash防止崩溃
        DefenseCrash.initialize();
        // 安装防火墙
        DefenseCrash.install(this);
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

    @Override
    public void onCaughtException(Thread thread, Throwable throwable, boolean b) {
        throwable.printStackTrace();
    }

    @Override
    public void onEnterSafeMode() {

    }

    @Override
    public void onMayBeBlackScreen(Throwable throwable) {

    }
}
