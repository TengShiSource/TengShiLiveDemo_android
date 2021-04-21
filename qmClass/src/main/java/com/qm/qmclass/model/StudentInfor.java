package com.qm.qmclass.model;

import android.view.View;

import java.io.Serializable;

/**
 * Created by lz on 2020/11/12.
 */
public class StudentInfor implements Serializable {
    private String userCode;
    private int userId;
    private String nickName;
    private String avatarUrl;
    private int expValue;
    private int studyCoin;
    private boolean isLianMai=false;
    private boolean isHuabiOn=false;

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

    public boolean isLianMai() {
        return isLianMai;
    }

    public void setLianMai(boolean lianMai) {
        isLianMai = lianMai;
    }

    public boolean isHuabiOn() {
        return isHuabiOn;
    }

    public void setHuabiOn(boolean huabiOn) {
        isHuabiOn = huabiOn;
    }
}
