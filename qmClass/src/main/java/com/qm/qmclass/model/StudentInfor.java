package com.qm.qmclass.model;

import android.view.View;

import java.io.Serializable;

/**
 * Created by lz on 2020/11/12.
 */
public class StudentInfor{
    private String userCode;
    private int userId;
    private String nickName;
    private String avatarUrl;
    private int expValue;
    private String expIcon;
    private int studyCoin;
    private int lianMaiState=3;//1 已连麦 2连麦中 3未连麦
    private boolean isHuabiOn=false;
    private boolean isCameraOn=false;//学生摄像头状态
    private boolean isMaikeOn=false;//学生麦克风状态
    private int SKTXNUM=0;//上课提醒次数

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getExpValue() {
        return expValue;
    }

    public void setExpValue(int expValue) {
        this.expValue = expValue;
    }

    public int getStudyCoin() {
        return studyCoin;
    }

    public void setStudyCoin(int studyCoin) {
        this.studyCoin = studyCoin;
    }

    public int getLianMaiState() {
        return lianMaiState;
    }

    public void setLianMaiState(int lianMaiState) {
        this.lianMaiState = lianMaiState;
    }

    public boolean isHuabiOn() {
        return isHuabiOn;
    }

    public void setHuabiOn(boolean huabiOn) {
        isHuabiOn = huabiOn;
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

    public int getSKTXNUM() {
        return SKTXNUM;
    }

    public void setSKTXNUM(int SKTXNUM) {
        this.SKTXNUM = SKTXNUM;
    }

    public String getExpIcon() {
        return expIcon;
    }

    public void setExpIcon(String expIcon) {
        this.expIcon = expIcon;
    }
}
