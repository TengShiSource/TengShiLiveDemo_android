package com.qm.qmclass.base;

import com.qm.qmclass.model.ChatContent;
import com.qm.qmclass.model.StudentInfor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LiveDataManager {
    private static volatile LiveDataManager INSTANCE;

    private HashMap<String, StudentInfor> allStudentsMap = new HashMap<>();
    private HashMap<String, StudentInfor> onLineStudentsMap = new HashMap<>();
    private HashMap<String, StudentInfor> offLineStudentsMap = new HashMap<>();
    private int Teacher_StudentListState=0;//教师端学生列表状态
    private boolean isCameraOn=true;//摄像头状态
    private boolean isMaikeOn=true;//麦克风状态
    private List<ChatContent> chatContentList=new ArrayList<ChatContent>();//聊天消息
    private boolean isOpenDanmu=false;//弹幕是否打开
    private boolean isJinYan=false;//禁言状态
    private String witchTools="1";
    private int textProgress=320;
    private int lineProgress=50;
    private int xingzhuang=0;
    private int textColor=0;
    private int lineColor=0;
    private int xian=1;
    private LiveDataManager() {
    }

    public static LiveDataManager getInstance() {
        if (INSTANCE == null) {
            synchronized (LiveDataManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LiveDataManager();
                }
            }
        }
        return INSTANCE;
    }

    public void destroyInstance() {
        INSTANCE = null;
    }

    public HashMap<String, StudentInfor> getAllStudentsMap() {
        return allStudentsMap;
    }

    public void setAllStudentsMap(HashMap<String, StudentInfor> allStudentsMap) {
        this.allStudentsMap = allStudentsMap;
    }

    public HashMap<String, StudentInfor> getOnLineStudentsMap() {
        return onLineStudentsMap;
    }

    public void setOnLineStudentsMap(HashMap<String, StudentInfor> onLineStudentsMap) {
        this.onLineStudentsMap = onLineStudentsMap;
    }

    public HashMap<String, StudentInfor> getOffLineStudentsMap() {
        return offLineStudentsMap;
    }

    public void setOffLineStudentsMap(HashMap<String, StudentInfor> offLineStudentsMap) {
        this.offLineStudentsMap = offLineStudentsMap;
    }

    public int getTeacher_StudentListState() {
        return Teacher_StudentListState;
    }

    public void setTeacher_StudentListState(int teacher_StudentListState) {
        Teacher_StudentListState = teacher_StudentListState;
    }

    public boolean isCameraOn() {
        return isCameraOn;
    }

    public void setCameraOn(boolean cameraOn) {
        isCameraOn = cameraOn;
    }

    public boolean isMaikeOn() {
        return isMaikeOn;
    }

    public void setMaikeOn(boolean maikeOn) {
        isMaikeOn = maikeOn;
    }

    public List<ChatContent> getChatContentList() {
        return chatContentList;
    }

    public void setChatContentList(List<ChatContent> chatContentList) {
        this.chatContentList = chatContentList;
    }

    public boolean isOpenDanmu() {
        return isOpenDanmu;
    }

    public void setOpenDanmu(boolean openDanmu) {
        isOpenDanmu = openDanmu;
    }

    public boolean isJinYan() {
        return isJinYan;
    }

    public void setJinYan(boolean jinYan) {
        isJinYan = jinYan;
    }

    public String getWitchTools() {
        return witchTools;
    }

    public void setWitchTools(String witchTools) {
        this.witchTools = witchTools;
    }

    public int getTextProgress() {
        return textProgress;
    }

    public void setTextProgress(int textProgress) {
        this.textProgress = textProgress;
    }

    public int getLineProgress() {
        return lineProgress;
    }

    public void setLineProgress(int lineProgress) {
        this.lineProgress = lineProgress;
    }

    public int getXingzhuang() {
        return xingzhuang;
    }

    public void setXingzhuang(int xingzhuang) {
        this.xingzhuang = xingzhuang;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getXian() {
        return xian;
    }

    public void setXian(int xian) {
        this.xian = xian;
    }
}
