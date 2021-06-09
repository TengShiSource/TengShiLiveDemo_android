package com.qm.qmclass.model;

import java.io.Serializable;

/**
 * Created by lz on 2020/11/12.
 */
public class StudentSignInfor{
    private String userCode;
    private String time;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
