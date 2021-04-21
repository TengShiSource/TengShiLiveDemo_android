package com.qm.qmclass.base;

import com.qm.qmclass.model.ChatContent;
import com.qm.qmclass.model.StudentInfor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static volatile DataManager INSTANCE;

    private String token = "";
    private String courseName;//课堂名称
    private String userName;//用户名
    private String startTime;//课堂开始时间
    private String endTime;//课堂结束时间
    private String appid;//应用ID
    private int courseId;//课堂ID
    private String role;//角色
    private int userid;
    private String teacherCode;//学生端教师code
    private String teacherName;//学生端教师Name
    private String teacherIcon;//学生端教师头像
    private String userCode;//用户课堂code

//    private HashMap<String, StudentInfor> allStudentsMap = new HashMap<>();
//    private HashMap<String, StudentInfor> onLineStudentsMap = new HashMap<>();
//    private HashMap<String, StudentInfor> offLineStudentsMap = new HashMap<>();
//    private int Teacher_StudentListState=0;//教师端学生列表状态
//    private boolean isCameraOn=true;//摄像头状态
//    private boolean isMaikeOn=true;//麦克风状态
//    private List<ChatContent> chatContentList=new ArrayList<ChatContent>();//聊天消息
//    private boolean isOpenDanmu=false;//弹幕是否打开
    private DataManager() {
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DataManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataManager();
                }
            }
        }
        return INSTANCE;
    }

    public void destroyInstance() {
        INSTANCE = null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherIcon() {
        return teacherIcon;
    }

    public void setTeacherIcon(String teacherIcon) {
        this.teacherIcon = teacherIcon;
    }
}
