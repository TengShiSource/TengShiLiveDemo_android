package com.qm.qmclass.utils;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
* activity管理
*/
public class ActivityCollector {

    private static Map<String, Activity> activities = new HashMap<>();

    //将Activity添加到队列中
    public static void addActivityToMap(Activity activity, String activityName) {
        activities.put(activityName, activity);
    }
    public static void removeActivity(String activityName){
        if (!activities.isEmpty()){
            activities.remove(activityName);
        }
    }
    //根据名字销毁制定Activity
    public static void destoryActivity(String activityName) {
        Set<String> keySet = activities.keySet();
        if (keySet.size() > 0) {
            for (String key : keySet) {
                if (activityName.equals(key)) {
                    activities.get(key).finish();
                }
            }
        }
    }

    public static void finishAll(){
        Set<String> keySet = activities.keySet();
        if (keySet.size() > 0) {
            for (String key : keySet) {
                if (!activities.get(key).isFinishing()){
                    activities.get(key).finish();
                }
            }
        }
    }
}
