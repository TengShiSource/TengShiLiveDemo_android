package com.qm.qmclass.base;

import android.view.View;

import com.qm.qmclass.fragment.VideoListFragment;
import com.qm.qmclass.model.ChatContent;
import com.qm.qmclass.model.StudentInfor;
import com.qm.qmclass.model.StudentSignInfor;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class LiveDataManager {

    private static volatile LiveDataManager INSTANCE;
    //所有学生数据（教师和学生共用）
    private LinkedHashMap<String, StudentInfor> allStudentsMap = new LinkedHashMap<>();
    //在线学生数据（教师和学生共用）
    private LinkedHashMap<String, StudentInfor> onLineStudentsMap = new LinkedHashMap<>();
    //离线学生数据（教师和学生共用）
    private LinkedHashMap<String, StudentInfor> offLineStudentsMap = new LinkedHashMap<>();
    //教师端TRTC视频
    private LinkedHashMap<String, TXCloudVideoView> trtcViewmap = new LinkedHashMap<>();
    //学生端拉流视频
    private LinkedHashMap<String, View> videoViewMap = new LinkedHashMap<>();
    //学生举手列表
    private List<String> jushouList = new ArrayList<>();
    //学生签到列表
    private List<StudentSignInfor> signedList = new ArrayList<>();
    //聊天消息
    private List<String> chatContentList=new ArrayList<String>();
    //教师端学生列表状态
    private int Teacher_StudentListState=0;
    //学生摄像头状态
    private boolean isCameraOn=false;
    //学生麦克风状态
    private boolean isMaikeOn=false;
    //老师摄像头状态
    private boolean isTCameraOn=true;
    //老师麦克风状态
    private boolean isTMaikeOn=true;
    //弹幕是否打开
    private boolean isOpenDanmu=false;
    //禁言状态
    private boolean isJinYan=false;
    //强制静音状态
    private boolean ismMandatory=false;
    //自己是否连麦（上台）
    private boolean myselfLianmai=false;
    //学生举手状态
    private boolean jushou=false;
    //白板工具选择项
    private String witchTools="1";
    //字体大小默认值
    private int textProgress=320;
    //画笔粗细默认值
    private int lineProgress=50;
    //画笔形状选项
    private int xingzhuang=0;
    //字体颜色选项
    private int textColor=0;
    //画笔颜色选项
    private int lineColor=0;
    private int xian=1;
    private VideoListFragment videoListFragment;
    //监控页面页数（第几页）
    private Integer pageCount=1;

    /*互动相关状态*/
    //点名时长默认一分钟
    private int DMTime=60;
    //问题类型
    private int answerType=1;//1 单选, 2 多选, 3 判断
    //问题模式
    private int questionMode=0;//0：普通提问 1：抢答
    //问题选项
    private List<String> questionOptions = new ArrayList<>();
    //问题答案
    private Set<String> questionAnswer=new TreeSet<>();
    //该题的荣誉分
    private int expValue=3;
    //答题时长限制
    private int timeLimit=60;
    //问题ID
    private Long questionId;



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

    public boolean isJushou() {
        return jushou;
    }

    public void setJushou(boolean jushou) {
        this.jushou = jushou;
    }

    public List<String> getJushouList() {
        return jushouList;
    }

    public void setJushouList(List<String> jushouList) {
        this.jushouList = jushouList;
    }

    public boolean isIsmMandatory() {
        return ismMandatory;
    }

    public void setIsmMandatory(boolean ismMandatory) {
        this.ismMandatory = ismMandatory;
    }

    public int getDMTime() {
        return DMTime;
    }

    public void setDMTime(int DMTime) {
        this.DMTime = DMTime;
    }

    public List<StudentSignInfor> getSignedList() {
        return signedList;
    }

    public void setSignedList(List<StudentSignInfor> signedList) {
        this.signedList = signedList;
    }

    public int getAnswerType() {
        return answerType;
    }

    public void setAnswerType(int answerType) {
        this.answerType = answerType;
    }

    public int getQuestionMode() {
        return questionMode;
    }

    public void setQuestionMode(int questionMode) {
        this.questionMode = questionMode;
    }

    public List<String> getQuestionOptions() {
        return questionOptions;
    }

    public void setQuestionOptions(List<String> questionOptions) {
        this.questionOptions = questionOptions;
    }

    public int getExpValue() {
        return expValue;
    }

    public void setExpValue(int expValue) {
        this.expValue = expValue;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Set<String> getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(Set<String> questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
