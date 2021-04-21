package com.qm.qmclass.qmmanager;

import android.app.Activity;
import android.content.Context;

/**
 * TICSDK业务管理类，主要负责课堂资源管理，互动管理
 */
public abstract class QMClassManager {

    /**
     * 1.1 获取TicManager的实例
     *
     */
    public static QMClassManager getInstance(){
        QMClassManager instance = null;
        synchronized (QMClassManager.class){
            instance = QMClassManagerImpl.sharedInstance();
        }
        return instance;
    }
    /**
     * 创建课堂 或加入课堂
     *
     */
//    public abstract void createClassroom(int appId,String courseName,String teacherPwd,String studentPwd,String startTime,String endTime,String courseOptions );
    public abstract void createOrJoinClassroom(String token);
    /**
     * 加入课堂
     *
     */
//    public abstract void joinClassroom(String token);

    /**
     * 1.2 初始化
     *
     * @param context
     *
     */
    public abstract void init(Context context);



}
