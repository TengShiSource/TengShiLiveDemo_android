package com.qm.qmclass.model;

/**
 * Created by lz on 2020/11/12.
 */
public class QuestionInfo {
    private int id;
    private Long courseId;
    private int studentId;
    private String studentNickName;
    private String pazzleUrl;
    private int resolveFlag;
    private String resolveTime;
    private int resolveTeacherId;
    private int appId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentNickName() {
        return studentNickName;
    }

    public void setStudentNickName(String studentNickName) {
        this.studentNickName = studentNickName;
    }

    public String getPazzleUrl() {
        return pazzleUrl;
    }

    public void setPazzleUrl(String pazzleUrl) {
        this.pazzleUrl = pazzleUrl;
    }

    public int getResolveFlag() {
        return resolveFlag;
    }

    public void setResolveFlag(int resolveFlag) {
        this.resolveFlag = resolveFlag;
    }

    public String getResolveTime() {
        return resolveTime;
    }

    public void setResolveTime(String resolveTime) {
        this.resolveTime = resolveTime;
    }

    public int getResolveTeacherId() {
        return resolveTeacherId;
    }

    public void setResolveTeacherId(int resolveTeacherId) {
        this.resolveTeacherId = resolveTeacherId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }
}
