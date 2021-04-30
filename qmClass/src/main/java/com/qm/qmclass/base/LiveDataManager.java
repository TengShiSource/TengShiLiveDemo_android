package com.qm.qmclass.base;

import android.view.View;

import com.qm.qmclass.fragment.VideoListFragment;
import com.qm.qmclass.model.ChatContent;
import com.qm.qmclass.model.StudentInfor;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class LiveDataManager {
    private static volatile LiveDataManager INSTANCE;

    private LinkedHashMap<String, StudentInfor> allStudentsMap = new LinkedHashMap<>();
    private LinkedHashMap<String, StudentInfor> onLineStudentsMap = new LinkedHashMap<>();
    private LinkedHashMap<String, StudentInfor> offLineStudentsMap = new LinkedHashMap<>();
    //教师端TRTC视频
    private LinkedHashMap<String, TXCloudVideoView> trtcViewmap = new LinkedHashMap<>();
    //学生端拉流视频
    private LinkedHashMap<String, View> videoViewMap = new LinkedHashMap<>();
    private int Teacher_StudentListState=0;//教师端学生列表状态
    private boolean isCameraOn=false;//学生摄像头状态
    private boolean isMaikeOn=false;//学生麦克风状态
    private boolean isTCameraOn=true;//老师摄像头状态
    private boolean isTMaikeOn=true;//老师麦克风状态
    private List<String> chatContentList=new ArrayList<String>();//聊天消息
    private boolean isOpenDanmu=false;//弹幕是否打开
    private boolean isJinYan=false;//禁言状态
    private boolean myselfLianmai=false;
    private String witchTools="1";
    private int textProgress=320;
    private int lineProgress=50;
    private int xingzhuang=0;
    private int textColor=0;
    private int lineColor=0;
    private int xian=1;
    private int boardNum=1;
    private VideoListFragment videoListFragment;
    private Integer pageCount=1;
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

    public LinkedHashMap<String, StudentInfor> getAllStudentsMap() {
        return allStudentsMap;
    }

    public void setAllStudentsMap(LinkedHashMap<String, StudentInfor> allStudentsMap) {
        this.allStudentsMap = allStudentsMap;
    }

    public LinkedHashMap<String, StudentInfor> getOnLineStudentsMap() {
        return onLineStudentsMap;
    }

    public void setOnLineStudentsMap(LinkedHashMap<String, StudentInfor> onLineStudentsMap) {
        this.onLineStudentsMap = onLineStudentsMap;
    }

    public LinkedHashMap<String, StudentInfor> getOffLineStudentsMap() {
        return offLineStudentsMap;
    }

    public void setOffLineStudentsMap(LinkedHashMap<String, StudentInfor> offLineStudentsMap) {
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

    public List<String> getChatContentList() {
        return chatContentList;
    }

    public void setChatContentList(List<String> chatContentList) {
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

    public int getBoardNum() {
        return boardNum;
    }

    public void setBoardNum(int boardNum) {
        this.boardNum = boardNum;
    }

    public boolean isMyselfLianmai() {
        return myselfLianmai;
    }

    public void setMyselfLianmai(boolean myselfLianmai) {
        this.myselfLianmai = myselfLianmai;
    }

    public LinkedHashMap<String, TXCloudVideoView> getTrtcViewmap() {
        return trtcViewmap;
    }

    public void setTrtcViewmap(LinkedHashMap<String, TXCloudVideoView> trtcViewmap) {
        this.trtcViewmap = trtcViewmap;
    }

    public VideoListFragment getVideoListFragment() {
        return videoListFragment;
    }

    public void setVideoListFragment(VideoListFragment videoListFragment) {
        this.videoListFragment = videoListFragment;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }


    public boolean isTCameraOn() {
        return isTCameraOn;
    }

    public void setTCameraOn(boolean TCameraOn) {
        isTCameraOn = TCameraOn;
    }

    public boolean isTMaikeOn() {
        return isTMaikeOn;
    }

    public void setTMaikeOn(boolean TMaikeOn) {
        isTMaikeOn = TMaikeOn;
    }

    public LinkedHashMap<String, View> getVideoViewMap() {
        return videoViewMap;
    }

    public void setVideoViewMap(LinkedHashMap<String, View> videoViewMap) {
        this.videoViewMap = videoViewMap;
    }
}
