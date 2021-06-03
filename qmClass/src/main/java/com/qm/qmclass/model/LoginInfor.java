package com.qm.qmclass.model;

import android.view.View;

import java.sql.Date;

/**
 * Created by lz on 2020/11/12.
 */
public class LoginInfor {
    private String token;
    private int userId;
    private String username;
    private long loginTime;
    private long expireTime;
    private String ipaddr;
    private String permissions;
    private String roles;
    private int courseId;
    private String userCode;
    private String userSig;
    private String userRole;
    private String avatar;
    private String studyCoin;
    private String expValue;
    private String expIcon;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStudyCoin() {
        return studyCoin;
    }

    public void setStudyCoin(String studyCoin) {
        this.studyCoin = studyCoin;
    }

    public String getExpValue() {
        return expValue;
    }

    public void setExpValue(String expValue) {
        this.expValue = expValue;
    }

    public String getExpIcon() {
        return expIcon;
    }

    public void setExpIcon(String expIcon) {
        this.expIcon = expIcon;
    }
}
